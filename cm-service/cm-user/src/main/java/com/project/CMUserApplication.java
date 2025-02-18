package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.project.mapper")
public class CMUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(CMUserApplication.class, args);
    }
}