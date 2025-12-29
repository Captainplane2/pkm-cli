package com.example.pkm_web.config;

import com.example.pkm_web.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Deprecated
 * 此拦截器已由 Spring Security 的 JwtAuthenticationFilter 替代。
 * 保留此文件仅用于参考或兼容旧逻辑。
 * 如果需要启用旧逻辑，请取消注释以下代码并在 WebConfig 中重新注册。
 */
//@Component
public class UserInterceptor implements HandlerInterceptor {
    //@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 模拟：假设当前登录用户是 ID 为 1 的用户
        // 在后续完善中，这里会从 Session 或 JWT Token 中解析用户 ID
        Long defaultUserId = 1L;
        UserContext.setCurrentUserId(defaultUserId);
        return true;
    }

    //@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 可以在此处添加请求处理后的逻辑
    }

    //@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}