package com.panut.centralbanking.customerservice;

import com.panut.centralbanking.customerservice.application.customertypelistelement.query.CustomerTypeListQuery;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListElementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
		ReactiveUserDetailsServiceAutoConfiguration.class
})
@EnableScheduling
public class CustomerserviceApplication {
	@Autowired
	private CustomerTypeListQuery customerTypeListQuery;

	public static void main(String[] args) {
		SpringApplication.run(CustomerserviceApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			String a = customerTypeListQuery.findIdGeneric(CustomerTypeEnum.REGULAR)
							.block();
			System.out.println(a);

			System.out.println("Customer service started//////////////");
		};
	}
}
