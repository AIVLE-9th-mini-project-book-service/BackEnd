#!/bin/bash
REPOSITORY=/var/www/backend-project
PROJECT_NAME=backend

echo "> 현재 구동 중인 애플리케이션 패키지 확인"
JAR_NAME=$(ls -tr $REPOSITORY/build/libs/*.jar | grep -v 'plain' | tail -n 1)

echo "> JAR Name: $JAR_NAME"
echo "> 현재 구동 중인 애플리케이션 PID 확인"
CURRENT_PID=$(pgrep -fl $JAR_NAME | grep java | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> 기존 애플리케이션 종료: kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> 새 애플리케이션 배포 및 실행 권한 추가"
chmod +x $JAR_NAME

echo "> $JAR_NAME 백그라운드로 실행 (로그는 nohup.out에 저장)"
nohup java -jar $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &