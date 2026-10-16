package com.panut.centralbanking.userservice;

import com.panut.centralbanking.userservice.application.userrolelistelement.service.UserRoleListService;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcAutoConfiguration;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
		ReactiveUserDetailsServiceAutoConfiguration.class,
})
@EnableScheduling
public class UserserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("Userservice started///////////");
		};
	}

}
