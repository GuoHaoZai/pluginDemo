#!/usr/bin/env bash

if [ -d cls ]; then
    rm -rf cls;
fi
mkdir cls

# 编译注解处理类
javac -cp "$JAVA_HOME/lib/tools.jar" ./*Processor.java ../anno/*.java -d cls

# 编译测试类
javac -cp cls -d cls/ -processor guohao.processor.GetterProcessor App.java

# 反编译测试类，查看编译结果
javap -p cls/guohao/processor/App.class

# 执行测试类
java -cp cls guohao.processor.App