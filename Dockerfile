FROM openjdk:21-jdk-slim

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV PROFILE=dev

ENTRYPOINT ["sh", "-c", "java -jar -Dspring.profiles.active=${PROFILE} /app.jar"]
