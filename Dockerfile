
FROM public.ecr.aws/docker/library/eclipse-temurin:17-jdk-jammy

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
