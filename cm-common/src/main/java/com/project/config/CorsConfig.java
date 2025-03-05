package com.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 放行哪些域名（使用具体的域名，避免和 allowCredentials 冲突）
                .allowedOriginPatterns("*") // 允许的前端域名
                // 允许的请求头
                .allowedHeaders("Authorization", "Content-Type") // 明确列出需要的请求头
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 是否允许携带凭证（如 cookies）
                .allowCredentials(true)
                // 预检请求缓存时间（单位：秒）
                .maxAge(3600);
    }
}
