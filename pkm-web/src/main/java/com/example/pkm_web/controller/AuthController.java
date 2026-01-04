package com.example.pkm_web.controller;

import com.example.pkm_web.model.User;
import com.example.pkm_web.service.UserAuthService;
import com.example.pkm_web.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = userAuthService.register(user);
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

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

    @DeleteMapping("/deregister")
    public ResponseEntity<Map<String, Object>> deregister() {
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
