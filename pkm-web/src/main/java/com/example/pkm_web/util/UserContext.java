package com.example.pkm_web.util;

import com.example.pkm_web.model.User;
import com.example.pkm_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserContext {
    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    @Autowired
    private static UserRepository userRepository;

    public static void setUserRepository(UserRepository repository) {
        userRepository = repository;
    }

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long getCurrentUserId() {
        Long userId = CURRENT_USER.get();
        if (userId != null) {
            return userId;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User springUser =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return extractUserIdFromUsername(springUser.getUsername());
        }

        return null;
    }

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

    public static Optional<Long> getCurrentUserIdOptional() {
        return Optional.ofNullable(getCurrentUserId());
    }

    public static void clear() {
        CURRENT_USER.remove();
        SecurityContextHolder.clearContext();
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}