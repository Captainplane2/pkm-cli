package com.example.pkm_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS配置类
 * 用于配置跨域资源共享(CORS)相关设置
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS跨域处理器
     * 创建WebMvcConfigurer实例来定义跨域请求的处理规则
     * @return WebMvcConfigurer CORS配置处理器
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 配置CORS映射规则：允许指定来源访问API接口
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}