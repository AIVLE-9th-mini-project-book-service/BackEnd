#!/bin/bash

JAR_PATH=/home/ec2-user/deploy/build/libs
JAR_FILE=$(ls $JAR_PATH/*.jar | tail -1)

sudo fuser -k 8080/tcp || true

nohup java -jar $JAR_FILE > /home/ec2-user/app.log 2>&1 &