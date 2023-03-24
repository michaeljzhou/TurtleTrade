package com.turtle.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.turtle.trade.mapper")
public class TurtleTradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurtleTradeApplication.class, args);
	}

}
