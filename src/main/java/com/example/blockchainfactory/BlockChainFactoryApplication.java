package com.example.blockchainfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableJpaRepositories
@EnableScheduling
@EnableAsync(proxyTargetClass=true)
public class BlockChainFactoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockChainFactoryApplication.class, args);
	}

}
