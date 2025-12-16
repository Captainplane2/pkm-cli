package com.example.pkm_web.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * API请求日志记录切面
 * 用于记录所有API请求的详细信息，包括请求方法、URL、参数、响应状态和处理时间
 */
@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

    // 定义切点：匹配所有controller包下的public方法
    @Pointcut("execution(public * com.example.pkm_web.controller.*.*(..))")
    public void apiPointcut() {
    }

    // 在方法执行前记录请求信息
    @Before("apiPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 记录请求URL
            logger.info("Request URL: {}", request.getRequestURL().toString());
            // 记录HTTP方法
            logger.info("HTTP Method: {}", request.getMethod());
            // 记录调用的类和方法
            logger.info("Class Method: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            // 记录请求IP
            logger.info("Request IP: {}", request.getRemoteAddr());
            // 记录请求参数
            logger.info("Request Args: {}", Arrays.toString(joinPoint.getArgs()));
            // 记录请求头信息
            logger.info("Request Headers:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                logger.info("  {}: {}", headerName, request.getHeader(headerName));
            }
        }
    }

    // 在方法成功返回后记录响应信息
    @AfterReturning("apiPointcut()")
    public void logResponse(JoinPoint joinPoint) {
        logger.info("Response: Method {}.{} executed successfully", 
                joinPoint.getSignature().getDeclaringTypeName(), 
                joinPoint.getSignature().getName());
    }

    // 在方法抛出异常时记录错误信息
    @AfterThrowing(pointcut = "apiPointcut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        logger.error("Exception in {}.{}: {}", 
                joinPoint.getSignature().getDeclaringTypeName(), 
                joinPoint.getSignature().getName(), 
                exception.getMessage(), 
                exception);
    }
}