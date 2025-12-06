package com.aftersports.aftersports;

import com.aftersports.aftersports.infra.config.AdminProperties;
import com.aftersports.aftersports.infra.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, AdminProperties.class})
public class AfterSportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfterSportsApplication.class, args);
    }
}
