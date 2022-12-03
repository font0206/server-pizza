FROM openjdk:11-jre-slim
WORKDIR /usr/src/myapp
COPY target/ApiGateway-0.0.1-SNAPSHOT.jar /usr/src/myapp/apigateway.jar
EXPOSE 9191
CMD [ "java", "-jar", "/usr/src/myapp/apigateway.jar" ]