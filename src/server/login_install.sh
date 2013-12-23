#!/bin/bash
cp ../../workspace/Login/src/Config.java ../../workspace/tmp/login
rm -rf ../../workspace/Login/src/*
cp -rf login/src/* ../../workspace/Login/src
cp -rf login/lib/* ../../workspace/Login/lib
cp -f ../../workspace/tmp/login/Config.java ../../workspace/Login/src

