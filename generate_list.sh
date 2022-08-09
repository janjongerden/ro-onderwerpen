#!/bin/zsh
set -e

curl --compressed https://www.rijksoverheid.nl/onderwerpen > onderwerpen.html

javac *.java

java FilterSubjectUrls > url_list.txt

mkdir -p subsubjects

cat url_list.txt | while read line ;
do
  sleep 5
  subsub=subsubjects/$(echo $line | cut -c42-).html
  echo fetching: $line
  curl --compressed $line > $subsub
done

java GenerateSubjectsHtml > index.html

DAY=$(date  +%Y-%m-%d)
mkdir -p archive
cp index.html "archive/$DAY.html"
