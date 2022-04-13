package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.example.Feign"})
public class SpringCloudDemo1Service1Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudDemo1Service1Application.class,args);
    }
}