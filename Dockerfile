# Dockerfile

FROM openjdk:21-jdk-slim

# 컨테이너의 작업 디렉토리를 /app 으로 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너의 /app 디렉토리로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# ✅ 수정된 부분: /app 디렉토리 내의 app.jar를 실행하도록 상대 경로 사용
ENTRYPOINT ["java","-jar","app.jar"]