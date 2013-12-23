#!/bin/bash
cp ../../workspace/Hall/src/Config.java ../../workspace/tmp/hall
rm -rf ../../workspace/Hall/src/*
cp -rf Hall/src/* ../../workspace/Hall/src
cp -rf Hall/lib/* ../../workspace/Hall/lib
cp -f ../../workspace/tmp/hall/Config.java ../../workspace/Hall/src

