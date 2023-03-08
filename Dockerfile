FROM openjdk:17
COPY build/libs/tesch_discord_bot-1.0.jar app.jar
ENTRYPOINT [ "java -jar app.jar" ]