FROM openjdk:23-ea-jre

LABEL authors="Lenny"

WORKDIR app

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
