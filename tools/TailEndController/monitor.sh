#!/bin/bash
java -Dorg.apache.logging.log4j.level=INFO -Djava.library.path=./lib/ -cp ./lib/*:./lib/commons-digester-1.7.zip:. PadtecMonitorJSON3

