FROM maven:3.6.0-jdk-11-slim AS build
COPY src /tmp/DemoChatroom/src
COPY pom.xml /tmp/DemoChatroom
RUN mvn -f /tmp/DemoChatroom/pom.xml clean package

FROM openjdk:8-jdk-alpine
MAINTAINER Vasilis Pagkalos
COPY --from=build /tmp/DemoChatroom/target/chatroom-0.0.1-SNAPSHOT.jar ~/DemoChatroom/chatroom-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","~/DemoChatroom/chatroom-0.0.1-SNAPSHOT.jar"]