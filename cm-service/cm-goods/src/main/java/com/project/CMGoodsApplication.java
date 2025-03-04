package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.project.mapper")
@EnableFeignClients
public class CMGoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CMGoodsApplication.class, args);
    }
}
