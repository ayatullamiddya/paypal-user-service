FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar paypal-user-service.jar
EXPOSE 8079
ENTRYPOINT ["java","-jar","/app/paypal-user-service.jar"]
