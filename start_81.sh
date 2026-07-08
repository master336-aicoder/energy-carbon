#!/bin/bash
JAR_PATH=/data/energy-carbon/app.jar
LOG_PATH=/data/energy-carbon/logs
JAVA=/data/jdk-21.0.1/bin/java
mkdir -p $LOG_PATH

echo "Starting backend on 192.168.1.81:8080 (dev profile)..."
nohup $JAVA -Xms512m -Xmx2g -jar $JAR_PATH --spring.profiles.active=dev > $LOG_PATH/backend.log 2>&1 &
echo $! > /data/energy-carbon/backend.pid
sleep 2
echo "Backend PID: $(cat /data/energy-carbon/backend.pid)"
