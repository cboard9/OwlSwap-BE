package com.cboard.owlswap.owlswap_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class OwlSwapBackendApplication
{

	public static void main(String[] args)
	{

		SpringApplication.run(OwlSwapBackendApplication.class, args);

	}

}