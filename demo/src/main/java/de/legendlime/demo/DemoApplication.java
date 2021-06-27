package de.legendlime.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.blockhound.BlockHound;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// BlockHound.install(); // throws an exception when a blocking call is involved in REST API
		SpringApplication.run(DemoApplication.class, args);
	}
}
