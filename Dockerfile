FROM openjdk:20
EXPOSE 8080
ADD ./target/*.jar ./app.jar
CMD java -jar app.jar