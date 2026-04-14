FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean install

EXPOSE 8080

ENTRYPOINT ["java","-jar","target/YoutubeTools-0.0.1-SNAPSHOT.jar"]