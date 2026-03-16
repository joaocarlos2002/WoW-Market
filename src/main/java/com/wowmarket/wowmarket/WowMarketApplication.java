package com.wowmarket.wowmarket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WowMarketApplication {
    private static Logger logger = LoggerFactory.getLogger(WowMarketApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WowMarketApplication.class, args);
        logger.info("[INFO] - API INICIADO");
	}
}
