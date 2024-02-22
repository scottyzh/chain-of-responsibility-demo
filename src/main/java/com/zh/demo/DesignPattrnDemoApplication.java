package com.zh.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DesignPattrnDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignPattrnDemoApplication.class, args);
    }

}
