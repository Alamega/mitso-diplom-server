FROM maven:latest AS build
COPY src /src
COPY pom.xml /
RUN mvn -f /pom.xml clean package

FROM openjdk:20
COPY --from=build /target/alamega-spring-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]