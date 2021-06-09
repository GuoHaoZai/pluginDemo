#!/usr/bin/env bash

if [ -d cls ]; then
    rm -rf cls;
fi
mkdir cls

javac -cp "$JAVA_HOME/lib/tools.jar" proc/* -d cls

javac -cp cls -d cls/ -processor guohao.code.lombok.proc.GetterProcessor Test.java