FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

RUN ./mvnw clean install

EXPOSE 8080

ENTRYPOINT ["java","-jar","target/YoutubeTools-0.0.1-SNAPSHOT.jar"]