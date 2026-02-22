package com.swp391_be.SWP391_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FsmSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FsmSystemApplication.class, args);
	}

}
