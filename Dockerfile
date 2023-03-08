#Build stage

FROM gradle:latest AS BUILD
WORKDIR /
COPY . . 
RUN gradle build

# Package stage

FROM openjdk:17
ENV JAR_NAME=discord-bot.jar
ENV APP_HOME=/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
ENTRYPOINT exec java -jar $APP_HOME/build/libs/tesch_discord_bot-1.0.jar

# Source: https://stackoverflow.com/questions/61108021/gradle-and-docker-how-to-run-a-gradle-build-within-docker-container