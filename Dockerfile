FROM gradle:latest AS BUILD
COPY . . 
RUN gradle build --no-daemon

FROM openjdk:17
COPY --from=BUILD /build/libs/tesch_discord_bot-1.0.jar app.jar
ENTRYPOINT ["java -jar app.jar"]