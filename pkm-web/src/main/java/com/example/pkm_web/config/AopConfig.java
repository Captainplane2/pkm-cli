package com.example.pkm_web.config;

import com.example.pkm_web.aspect.LoggingAspect;
import com.example.pkm_web.aspect.PerformanceMonitorAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(
    proxyTargetClass = true,     // 使用CGLIB代理
    exposeProxy = true          // 暴露代理对象，解决内部调用问题
)
@Slf4j
public class AopConfig {
    @Bean
    @Order(1)  // 定义切面执行顺序，日志记录优先
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
    
    @Bean
    @Order(2)  // 性能监控切面在日志记录之后执行
    public PerformanceMonitorAspect performanceMonitorAspect() {
        return new PerformanceMonitorAspect();
    }
}