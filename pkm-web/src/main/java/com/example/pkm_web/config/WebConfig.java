package com.example.pkm_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // UserInterceptor 已由 Spring Security 的 JwtAuthenticationFilter 替代
    // 如果需要兼容旧逻辑，可以在此处添加新的拦截器
    
}