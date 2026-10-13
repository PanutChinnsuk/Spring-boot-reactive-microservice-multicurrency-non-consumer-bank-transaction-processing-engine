package com.panut.centralbanking.customerservice;

import com.panut.centralbanking.appcore.exceptions.handler.GlobalMonoExceptionHandler;
import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.api.request.CustomerCreateRequest;
import com.panut.centralbanking.customerservice.api.response.CustomerCreatedData;
import com.panut.centralbanking.customerservice.application.customer.service.CustomerCreateService;
import com.panut.centralbanking.customerservice.application.customertypelist.implement.CustomerTypeListServiceImpl;
import com.panut.centralbanking.customerservice.application.customertypelist.service.CustomerTypeListService;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerPhoneRepository;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.PessimisticLockingFailureException;
import reactor.core.publisher.Mono;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
		ReactiveUserDetailsServiceAutoConfiguration.class
})
public class CustomerserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomerserviceApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {


			System.out.println("Customer service started//////////////");
		};
	}
}
