package com.example.pkm_web.aspect;

import com.example.pkm_web.annotation.Validation;
import com.example.pkm_web.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 参数验证切面
 * 用于拦截带有@Validation注解的方法，对其参数进行验证
 */
@Aspect
@Component
@Slf4j
public class ValidationAspect {
    
    @Autowired
    private Validator validator;

    /**
     * 定义切入点：匹配所有带有@Validation注解的方法
     */
    @Pointcut("@annotation(com.example.pkm_web.annotation.Validation)")
    public void validationPointcut() {
    }

    /**
     * 前置通知：在方法执行前进行参数验证
     */
    @Before("validationPointcut()")
    public void validateParameters(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取@Validation注解
        Validation validation = method.getAnnotation(Validation.class);
        
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        
        log.debug("对方法 {} 进行参数验证", method.getName());
        
        // 遍历参数进行验证
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String paramName = parameterNames[i];
            
            // 1. 检查是否允许空值
            if (!validation.allowEmpty()) {
                validateNotEmpty(arg, paramName);
            }
            
            // 2. 如果参数是字符串类型，检查长度和正则表达式
            if (arg instanceof String) {
                String strArg = (String) arg;
                
                // 检查长度
                if (validation.maxLength() > 0) {
                    validateMaxLength(strArg, paramName, validation.maxLength());
                }
                
                // 检查正则表达式
                if (!validation.regex().isEmpty()) {
                    validateRegex(strArg, paramName, validation.regex());
                }
            }
            
            // 3. 使用JSR-303验证器进行验证
            if (arg != null) {
                validateWithJsr303(arg, paramName, validation.groups());
            }
        }
        
        log.debug("方法 {} 参数验证通过", method.getName());
    }

    /**
     * 验证参数是否为空
     */
    private void validateNotEmpty(Object arg, String paramName) {
        if (arg == null) {
            log.error("参数 {} 不能为空", paramName);
            // 根据参数名生成更友好的错误信息
            String friendlyMessage = getFriendlyMessage(paramName, "不能为空");
            throw new ValidationException(paramName, friendlyMessage);
        }
        
        if (arg instanceof String && ((String) arg).trim().isEmpty()) {
            log.error("参数 {} 不能为空字符串", paramName);
            // 根据参数名生成更友好的错误信息
            String friendlyMessage = getFriendlyMessage(paramName, "不能为空");
            throw new ValidationException(paramName, friendlyMessage);
        }
    }
    
    /**
     * 根据参数名生成更友好的错误信息
     */
    private String getFriendlyMessage(String paramName, String reason) {
        // 针对不同的参数名生成更具体的错误信息
        switch (paramName) {
            case "title":
                return "笔记标题不能为空";
            case "content":
                return "笔记内容不能为空";
            case "id":
                return "笔记ID不能为空";
            case "tag":
                return "标签名称不能为空";
            case "categoryId":
                return "分类ID不能为空";
            default:
                return "参数" + paramName + reason;
        }
    }

    /**
     * 验证字符串参数的最大长度
     */
    private void validateMaxLength(String arg, String paramName, int maxLength) {
        if (arg.length() > maxLength) {
            log.error("参数 {} 长度不能超过 {}，当前长度为 {}", paramName, maxLength, arg.length());
            throw new ValidationException(paramName, "参数长度不能超过 " + maxLength);
        }
    }

    /**
     * 验证字符串参数是否符合正则表达式
     */
    private void validateRegex(String arg, String paramName, String regex) {
        if (!Pattern.matches(regex, arg)) {
            log.error("参数 {} 不符合正则表达式 {}", paramName, regex);
            throw new ValidationException(paramName, "参数格式不符合要求");
        }
    }
    
    /**
     * 使用JSR-303验证器进行验证
     */
    private void validateWithJsr303(Object arg, String paramName, Class<?>[] groups) {
        Set<ConstraintViolation<Object>> violations = validator.validate(arg, groups);
        if (!violations.isEmpty()) {
            // 收集所有验证错误信息
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Object> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                          .append(": ")
                          .append(violation.getMessage())
                          .append("; ");
            }
            
            String error = errorMessage.toString().trim();
            if (error.endsWith(";")) {
                error = error.substring(0, error.length() - 1);
            }
            
            log.error("参数 {} JSR-303验证失败: {}", paramName, error);
            throw new ValidationException(paramName, error);
        }
    }
}
