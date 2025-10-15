#!/bin/bash
# Generate JavaDoc for all project packages into docs/javadoc
set -e
cd "$(dirname "$0")"

echo "Generating JavaDoc to docs/javadoc (de, UTF-8) ..."
mkdir -p docs/javadoc
javadoc \
  -d docs/javadoc \
  -sourcepath src \
  -subpackages main:entities:levels:gameplay:ui:audio:utils:inputs \
  -author -version \
  -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 \
  -locale de \
  -Xdoclint:all,-missing

echo "JavaDoc generated at docs/javadoc/index.html"
