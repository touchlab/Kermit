#! /bin/bash
set -e

for i in `ls samples`; do
echo "---- $i ----"
cd samples/$i
./gradlew ciTest --no-daemon --stacktrace --build-cache -PexcludeX64Targets=true
cd ../..
done
