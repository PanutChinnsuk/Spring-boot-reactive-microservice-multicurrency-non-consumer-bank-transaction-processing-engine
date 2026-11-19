package com.panut.centralbanking.accountservice;

import com.panut.centralbanking.accountservice.api.service.transaction.TransactionClearingServiceController;
import com.panut.centralbanking.appcore.util.ListIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
		ReactiveUserDetailsServiceAutoConfiguration.class
})
@EnableScheduling
public class AccountserviceApplication {


	public static void main(String[] args) {
		SpringApplication.run(AccountserviceApplication.class, args);
	}



	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {


			System.out.println("Account service started///////////////////////");
		};
	}
}
