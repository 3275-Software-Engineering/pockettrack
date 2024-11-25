FROM openjdk:17-jdk

LABEL authors="Lenny"

WORKDIR app

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
