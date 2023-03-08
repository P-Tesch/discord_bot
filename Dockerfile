FROM gradle:latest AS BUILD
COPY . . 
RUN gradle build --no-daemon

FROM openjdk:17
COPY --from=BUILD ./build/libs/*.jar app.jar
ENTRYPOINT ["java -jar app.jar"]