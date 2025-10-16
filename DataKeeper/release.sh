#!/bin/bash
set -e
cd "$(dirname "$0")"

JRE_VERSION=17
WITH_WIN=false
WITH_LINUX=false
WITH_MAC=false

for arg in "$@"; do
  case "$arg" in
    --with-jre-win) WITH_WIN=true ;;
    --with-jre-linux) WITH_LINUX=true ;;
    --with-jre-mac) WITH_MAC=true ;;
  esac
done

echo "==> Building package..."
chmod +x package.sh
./package.sh

echo "==> Ensuring JavaDoc archive (optional)..."
if [ ! -f dist/javadoc.zip ]; then
  if [ -x ./javadoc.sh ]; then ./javadoc.sh || true; fi
  if [ -x ./docs/package_javadoc.sh ]; then ./docs/package_javadoc.sh || true; fi
fi

mkdir -p dist/bundles/windows dist/bundles/linux dist/bundles/macos

copy_common() {
  local target="$1"
  mkdir -p "$target"
  cp -f dist/DATA_KEEPER.jar "$target/" 2>/dev/null || true
  cp -f README_RELEASE.md "$target/" 2>/dev/null || true
  # Copy JavaDoc zip alongside (optional)
  if [ -f dist/javadoc.zip ]; then cp -f dist/javadoc.zip "$target/"; fi
}

echo "==> Preparing per-OS bundles..."
# Windows bundle
copy_common dist/bundles/windows
cp -f dist/Run-DataKeeper.bat dist/bundles/windows/ 2>/dev/null || true
cp -f dist/Run-DataKeeper.vbs dist/bundles/windows/ 2>/dev/null || true

# Linux bundle
copy_common dist/bundles/linux
cp -f dist/Run-DataKeeper.sh dist/bundles/linux/ 2>/dev/null || true
cp -f dist/DataKeeper.desktop dist/bundles/linux/ 2>/dev/null || true

# macOS bundle
copy_common dist/bundles/macos
cp -f dist/DataKeeper.command dist/bundles/macos/ 2>/dev/null || true

fetch_jre() {
  # Args: os (windows|linux|mac), arch (x64), target_dir
  local os="$1" arch="$2" target="$3"
  local url="https://api.adoptium.net/v3/binary/latest/${JRE_VERSION}/ga/${os}/${arch}/jre/hotspot/normal/eclipse"
  echo "   -> Downloading JRE ${JRE_VERSION} for ${os}/${arch} ..."
  mkdir -p "$target/tmp"
  local outfile="$target/tmp/jre-${os}-${arch}"
  # Choose extension per OS
  local ext
  if [ "$os" = "windows" ]; then ext="zip"; else ext="tar.gz"; fi
  outfile="$outfile.$ext"
  if command -v curl >/dev/null 2>&1; then
    curl -L "$url" -o "$outfile"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$outfile" "$url"
  else
    echo "curl/wget not found; cannot fetch JRE automatically" >&2
    return 1
  fi
  echo "   -> Extracting JRE ..."
  if [ "$ext" = "zip" ]; then
    if command -v unzip >/dev/null 2>&1; then
      unzip -q "$outfile" -d "$target/tmp"
    else
      echo "unzip not found; cannot extract Windows JRE" >&2
      return 1
    fi
  else
    tar -xzf "$outfile" -C "$target/tmp"
  fi
  # Move extracted folder to target/jre
  local extracted
  extracted=$(find "$target/tmp" -maxdepth 1 -mindepth 1 -type d | head -n 1)
  if [ -z "$extracted" ]; then echo "Extraction failed" >&2; return 1; fi
  rm -rf "$target/jre"
  mv "$extracted" "$target/jre"
  rm -rf "$target/tmp"
  echo "   -> JRE ready at $target/jre"
}

if [ "$WITH_WIN" = true ]; then
  fetch_jre windows x64 dist/bundles/windows || echo "(warn) Skipping Windows JRE embed"
fi
if [ "$WITH_LINUX" = true ]; then
  fetch_jre linux x64 dist/bundles/linux || echo "(warn) Skipping Linux JRE embed"
fi
if [ "$WITH_MAC" = true ]; then
  fetch_jre mac x64 dist/bundles/macos || echo "(warn) Skipping macOS JRE embed"
fi

echo "==> Creating archives..."
pushd dist/bundles >/dev/null
if command -v zip >/dev/null 2>&1; then
  rm -f windows.zip linux.zip macos.zip Release_ALL.zip
  zip -q -r windows.zip windows
  zip -q -r linux.zip linux
  zip -q -r macos.zip macos
  zip -q -r Release_ALL.zip windows linux macos
  echo "Wrote dist/bundles/windows.zip, linux.zip, macos.zip, Release_ALL.zip"
else
  echo "zip not found; bundles created in dist/bundles/ folders"
fi
popd >/dev/null

echo "Done."
