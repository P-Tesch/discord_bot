FROM gradle:8.0.2-jdk17-alpine AS gradlebuilder
WORKDIR /home
COPY . /home
RUN gradle build

FROM openjdk:17-alpine
COPY --from=gradlebuilder home/build/libs/*.jar tesch_discord_bot-1.0.jar
ENTRYPOINT ["java", "-jar", "tesch_discord_bot-1.0.jar"]