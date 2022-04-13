package org.example.SpringCloudDemo1EurekaServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer //当前使用eureka的server
public class SpringCloudDemo1EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudDemo1EurekaServerApplication.class, args);
	}

}
