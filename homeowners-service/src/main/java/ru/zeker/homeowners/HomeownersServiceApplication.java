package ru.zeker.homeowners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class HomeownersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeownersServiceApplication.class, args);
	}

}
