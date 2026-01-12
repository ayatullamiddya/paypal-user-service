package com.paypal.paypal_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableFeignClients
@EnableMethodSecurity
public class PaypalUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaypalUserServiceApplication.class, args);
	}

}
