package com.zh.demo.config;

import com.zh.demo.designpattern.chain.AbstractChainContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DesignPatternAutoConfiguration {

    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }
}