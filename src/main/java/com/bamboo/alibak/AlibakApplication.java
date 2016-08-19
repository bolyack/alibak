package com.bamboo.alibak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
@Configuration
public class AlibakApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlibakApplication.class, args);
	}
}
