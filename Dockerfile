FROM openjdk:14-jdk-alpine

ADD qr-code-attendance-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar" ]
