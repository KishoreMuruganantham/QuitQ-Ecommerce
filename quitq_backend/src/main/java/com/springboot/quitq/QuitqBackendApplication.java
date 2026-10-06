package com.springboot.quitq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class QuitqBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuitqBackendApplication.class, args);
	}

}
