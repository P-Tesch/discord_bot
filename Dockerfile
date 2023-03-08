FROM gradle:8.0.2-jdk17-alpine AS gradlebuilder
COPY . .
RUN gradle build
COPY ./build/libs/tesch_discord_bot-1.0.jar tesch_discord_bot-1.0.jar
ENTRYPOINT ["java", "-jar", "tesch_discord_bot-1.0.jar"]