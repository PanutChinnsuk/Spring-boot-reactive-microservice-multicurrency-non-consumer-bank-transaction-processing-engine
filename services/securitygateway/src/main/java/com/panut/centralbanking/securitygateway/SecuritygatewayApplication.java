package com.panut.centralbanking.securitygateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class,
ReactiveUserDetailsServiceAutoConfiguration.class})
public class SecuritygatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuritygatewayApplication.class, args);
	}



	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("Security gateway started//////////////");
		};
	}
}
