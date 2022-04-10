#!/bin/zsh
set -e

curl --compressed https://www.rijksoverheid.nl/onderwerpen > onderwerpen.html

javac FilterSubjectUrls.java
java FilterSubjectUrls > url_list.txt

cat url_list.txt | while read line ;
do
  sleep 5
  subsub=subsubjects/$(echo $line | cut -c42-).html
  echo fetching: $line
  curl --compressed $line > $subsub
done

cp top.html index.html

javac GenerateSubjectsHtml.java
java GenerateSubjectsHtml >> index.html

cat bottom.html >> index.html

DAY=$(date  +%m-%d)
mkdir -p archive
cp index.html "archive/$DAY.html"
