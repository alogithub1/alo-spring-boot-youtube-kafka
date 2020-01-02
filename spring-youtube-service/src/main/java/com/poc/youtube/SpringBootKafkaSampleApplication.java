package com.poc.youtube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The SpringBootKafkaSampleApplication class for spring boot application.
 * 
 * @version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SpringBootKafkaSampleApplication {

	static final String TOPIC_TEST_1 = "test_1";
	static final String TOPIC_TEST_2 = "test_2";

	public static void main(String[] args) {
		SpringApplication.run(SpringBootKafkaSampleApplication.class, args);
	}
}