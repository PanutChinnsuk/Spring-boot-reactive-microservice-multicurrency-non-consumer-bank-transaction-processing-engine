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
			var sql = new StringBuilder("""
                       UPDATE account_balances 
                       SET 
                       """);



			int size = 10;
			var balance = new StringBuilder("""
                            balance = CASE id
                            """);

			for (int i = 0; i < size; i++) {
				balance.append("WHEN ? THEN ? \n");

				if (!ListIndex.isNotLast(i, size)) {
					balance.append("END");
				}
			}
			sql.append(balance);



			System.out.println(sql.toString());




			System.out.println("Account service started///////////////////////");
		};
	}
}
