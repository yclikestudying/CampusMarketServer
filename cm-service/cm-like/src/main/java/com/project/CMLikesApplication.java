package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.project.mapper")
@EnableFeignClients("com.project.api")
public class CMLikesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CMLikesApplication.class, args);
    }
}
