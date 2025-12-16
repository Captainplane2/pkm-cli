package com.example.pkm_web.aspect;

import com.example.pkm_web.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    // 切点：所有标记@OperationLog的方法
    @Pointcut("@annotation(operationLog)")
    public void operationLogPointcut(OperationLog operationLog) {}
    
    @Around("operationLogPointcut(operationLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // 记录开始日志
        log.info("【操作开始】方法: {}.{}, 操作: {}, 描述: {}", 
                className, methodName, operationLog.type(), operationLog.value());
        
        long startTime = System.currentTimeMillis();
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // 记录成功日志
            log.info("【操作成功】方法: {}.{}, 耗时: {}ms", 
                    className, methodName, endTime - startTime);
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();

            // 记录失败日志
            log.error("【操作失败】方法: {}.{}, 耗时: {}ms, 异常: {}", 
                    className, methodName, endTime - startTime, e.getMessage(), e);
            
            throw e;
        }
    }
}
