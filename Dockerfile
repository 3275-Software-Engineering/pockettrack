FROM openjdk:21-jdk

LABEL authors="Lenny"

WORKDIR app

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
