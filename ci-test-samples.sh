#! /bin/bash
set -e

for i in `ls samples`; do
echo "---- $i ----"
if [ "$i" != "sample-chisel" ]
then
cd samples/$i
./gradlew ciTest
cd ../..
fi
done
