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

echo "> CloudWatch Agent 설치 여부 확인 및 진행"
if ! rpm -qa | grep -q amazon-cloudwatch-agent; then
    wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
    
    sudo rpm -Uvh amazon-cloudwatch-agent.rpm
fi

echo "> CloudWatch Agent 설정 파일(config.json) 생성"
cat << 'EOF' > /opt/aws/amazon-cloudwatch-agent/bin/config.json
{
  "agent": {
    "metrics_collection_interval": 60,
    "run_as_user": "root"
  },
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/var/www/backend-project/nohup.out",
            "log_group_name": "user132-backend-log",
            "log_stream_name": "{instance_id}",
            "retention_in_days": 7
          }
        ]
      }
    }
  }
}
EOF

echo "> CloudWatch Agent 설정 적용 및 강제 재시작"
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
-a fetch-config -m ec2 -s -c file:/opt/aws/amazon-cloudwatch-agent/bin/config.json
# =========================================================================

echo "> 새 애플리케이션 배포 및 실행 권한 추가"
chmod +x $JAR_NAME

echo "> $JAR_NAME 백그라운드로 실행 (로그는 nohup.out에 저장)"
nohup java -jar $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &