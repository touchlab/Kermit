#! /bin/bash

for i in `ls samples`; do
echo "---- $i ----"
cd samples/$i
./gradlew clean ciTest --quiet
cd ../..
done
