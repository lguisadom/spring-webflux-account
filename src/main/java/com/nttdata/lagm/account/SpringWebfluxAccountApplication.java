package com.nttdata.lagm.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringWebfluxAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxAccountApplication.class, args);
	}

}
