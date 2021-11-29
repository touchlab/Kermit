#! /bin/bash
set -e

for i in `ls samples`; do
echo "---- $i ----"
cd samples/$i
./gradlew ciTest
cd ../..
done
