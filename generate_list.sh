#!/bin/zsh
set -e

curl --compressed https://www.rijksoverheid.nl/onderwerpen>onderwerpen.txt

cp top.html index.html

javac ParseSubjects.java
java ParseSubjects >> index.html

cat bottom.html >> index.html

DAY=$(date  +%m-%d)
mkdir -p archive
cp index.html "archive/$DAY.html"
