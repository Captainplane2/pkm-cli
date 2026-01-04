package com.example.pkm_web.util;

import com.example.pkm_web.model.User;
import com.example.pkm_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 用户上下文工具类，用于管理当前登录用户的上下文信息
 * 提供获取当前用户ID、用户名等方法，并支持ThreadLocal和Spring Security的用户信息获取
 */
@Component
public class UserContext {
    /**
     * 用于存储当前线程用户ID的ThreadLocal变量
     */
    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository repository) {
        UserContext.userRepository = repository;
    }

    /**
     * 设置当前线程的用户ID
     * @param userId 用户ID
     */
    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    /**
     * 获取当前用户ID，优先从ThreadLocal获取，如果ThreadLocal中没有则从Spring Security上下文中获取
     * @return 当前用户ID，如果获取失败则返回null
     */
    public static Long getCurrentUserId() {
        Long userId = CURRENT_USER.get();
        if (userId != null) {
            return userId;
        }

        // 从Spring Security上下文中获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User springUser =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return extractUserIdFromUsername(springUser.getUsername());
        }

        return null;
    }

    /**
     * 根据用户名从数据库中提取用户ID
     * @param username 用户名
     * @return 用户ID，如果用户不存在或查询失败则返回null
     */
    private static Long extractUserIdFromUsername(String username) {
        if (userRepository == null) {
            return null;
        }
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            return userOpt.map(User::getId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前用户ID的Optional包装
     * @return 包含当前用户ID的Optional对象
     */
    public static Optional<Long> getCurrentUserIdOptional() {
        return Optional.ofNullable(getCurrentUserId());
    }

    /**
     * 清除当前线程的用户上下文信息
     * 包括ThreadLocal中的用户ID and Spring Security 上下文
     */
    public static void clear() {
        CURRENT_USER.remove();
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取当前认证用户的用户名
     * @return 当前用户名，如果未认证则返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
