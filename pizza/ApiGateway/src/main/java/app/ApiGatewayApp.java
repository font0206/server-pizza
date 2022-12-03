package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@EnableEurekaClient
@SpringBootApplication
public class ApiGatewayApp {
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApp.class, args);
	}
}
