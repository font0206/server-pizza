FROM openjdk:11-jre-slim
WORKDIR /usr/src/myapp
COPY target/CartService-0.0.1-SNAPSHOT.jar /usr/src/myapp/cart.jar
EXPOSE 8082
CMD [ "java", "-jar", "/usr/src/myapp/cart.jar" ]