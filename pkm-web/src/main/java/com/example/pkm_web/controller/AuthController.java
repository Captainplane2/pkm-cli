package com.example.pkm_web.controller;

import com.example.pkm_web.model.User;
import com.example.pkm_web.service.UserAuthService;
import com.example.pkm_web.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户注册、登录和注销相关的API请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 用户注册接口
     * @param user 用户注册信息，包含用户名、密码等
     * @return ResponseEntity<Map<String, Object>> 包含注册结果的响应实体
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = userAuthService.register(user);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 用户登录接口
     * @param loginRequest 包含用户名和密码的登录请求数据
     * @return ResponseEntity<Map<String, Object>> 包含登录结果的响应实体
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 验证用户名和密码是否为空
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户名和密码不能为空"
            ));
        }

        Map<String, Object> result = userAuthService.login(username, password);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 用户注销接口
     * 删除当前登录用户的信息
     * @return ResponseEntity<Map<String, Object>> 包含注销结果的响应实体
     */
    @DeleteMapping("/deregister")
    public ResponseEntity<Map<String, Object>> deregister() {
        // 获取当前登录用户的ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "请先登录"
            ));
        }

        Map<String, Object> result = userAuthService.deleteUser(currentUserId);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}