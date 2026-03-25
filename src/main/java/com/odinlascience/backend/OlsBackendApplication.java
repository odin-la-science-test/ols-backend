package com.odinlascience.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OlsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlsBackendApplication.class, args);
	}

}
