package com.panut.centralbanking.accountservice;

import com.panut.centralbanking.accountservice.application.kafka.templates.JustProcessedBalanceChangeEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Set;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
		ReactiveUserDetailsServiceAutoConfiguration.class
})
@EnableScheduling
public class AccountserviceApplication {
	@Autowired
	private JustProcessedBalanceChangeEventsProducer producer;

	public static void main(String[] args) {
		SpringApplication.run(AccountserviceApplication.class, args);
	}



	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			producer.unmandatorySend(Set.of("a","b","c"));

			System.out.println("Account service started///////////////////////");
		};
	}
}
