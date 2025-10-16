#!/bin/bash
set -e
cd "$(dirname "$0")/.."

if [ ! -d docs/javadoc ]; then
  echo "docs/javadoc not found. Run ./javadoc.sh first." >&2
  exit 1
fi

mkdir -p dist
cd docs
zip -r ../dist/javadoc.zip javadoc >/dev/null
echo "Wrote dist/javadoc.zip. Open javadoc/index.html inside the zip (extract first to keep CSS/JS working)."
