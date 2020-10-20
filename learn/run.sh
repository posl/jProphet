#!/bin/bash

python3 collect.py # GitHubトークンをcollect-config.jsonに記入してください
cd ..
./gradlew run -Pargs="-t"
cd learn
python3 learn.py # result/para.csvにパラメータが出力される