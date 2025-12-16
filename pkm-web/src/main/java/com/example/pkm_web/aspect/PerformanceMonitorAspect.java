package com.example.pkm_web.aspect;

import com.example.pkm_web.annotation.PerformanceMonitor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

/**
 * 性能监控切面
 * 用于监控方法执行时间并记录性能数据
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitorAspect {

    /**
     * 定义性能监控切点，匹配所有使用@PerformanceMonitor注解的方法
     */
    @Pointcut("@annotation(com.example.pkm_web.annotation.PerformanceMonitor)")
    public void performanceMonitorPointcut() {}

    /**
     * 性能监控环绕通知
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("performanceMonitorPointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名和注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PerformanceMonitor annotation = method.getAnnotation(PerformanceMonitor.class);
        
        // 创建方法描述
        String methodDesc = annotation.value().isEmpty() ? 
                String.format("%s.%s", joinPoint.getTarget().getClass().getSimpleName(), method.getName()) : 
                annotation.value();
        
        // 记录方法参数
        String paramsInfo = annotation.recordParams() ? 
                String.format("参数: %s", java.util.Arrays.toString(joinPoint.getArgs())) : 
                "";
        
        // 开始计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } finally {
            // 停止计时并计算执行时间
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            
            // 记录返回值
            String resultInfo = annotation.recordResult() ? 
                    String.format("返回值: %s", result != null ? result.toString() : "null") : 
                    "";
            
            // 根据执行时间选择日志级别
            if (executionTime > annotation.threshold()) {
                log.warn("[性能警告] 方法 {} 执行耗时: {}ms (超过阈值 {}ms) {}{}", 
                        methodDesc, executionTime, annotation.threshold(), 
                        paramsInfo.isEmpty() ? "" : " " + paramsInfo, 
                        resultInfo.isEmpty() ? "" : " " + resultInfo);
            } else {
                log.info("[性能监控] 方法 {} 执行耗时: {}ms {}{}", 
                        methodDesc, executionTime, 
                        paramsInfo.isEmpty() ? "" : " " + paramsInfo, 
                        resultInfo.isEmpty() ? "" : " " + resultInfo);
            }
        }
    }
}