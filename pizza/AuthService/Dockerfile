FROM openjdk:11-jre-slim
WORKDIR /usr/src/myapp
COPY target/AuthService-0.0.1-SNAPSHOT.jar /usr/src/myapp/auth.jar
EXPOSE 8081
CMD [ "java", "-jar", "/usr/src/myapp/auth.jar" ]