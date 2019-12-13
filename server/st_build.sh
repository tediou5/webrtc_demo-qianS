#!/bin/bash

gradle clean
gradle build 

echo -e "\n---- output jar   ----"
find . -name *.jar |grep -v REF_ |grep build 




# [Theodore: 2019-09-20] sed SPACE regex: 
# - https://stackoverflow.com/questions/15509536/regex-space-character-in-sed
echo -e "\n---- genlib version ----"
find genlib -name StVersion.java -exec grep 'return' {} \; | sed -e 's/return//g'  -e 's/[[:space:]]//g' -e 's/;//g'

echo -e "\n---- server version ----"
echo -e "TODO..."
find server -name StVersion.java -exec grep 'return' {} \; | sed 's/return//g'  | sed 's/[[:space:]]//g' 

echo -e "\nDONE!\n"


