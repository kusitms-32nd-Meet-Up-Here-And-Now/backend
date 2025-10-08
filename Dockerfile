# ===== 1️⃣ Base Image =====
FROM openjdk:21-jdk-slim

# ===== 2️⃣ 빌드 산출물 복사 =====
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# ===== 3️⃣ 기본 프로파일 설정 =====
# → 실행 시 -e PROFILE=dev or prod 로 덮어쓰기 가능
ENV PROFILE=dev

# ===== 4️⃣ 실행 명령 =====
ENTRYPOINT ["sh", "-c", "java -jar -Dspring.profiles.active=${PROFILE} /app.jar"]
