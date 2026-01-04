package com.example.pkm_web.service;

import com.example.pkm_web.model.User;
import com.example.pkm_web.repository.CategoryRepository;
import com.example.pkm_web.repository.NoteRepository;
import com.example.pkm_web.repository.TagRepository;
import com.example.pkm_web.repository.UserRepository;
import com.example.pkm_web.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserAuthService {     //实现用户注册和登录业务

    @Autowired      //@Autowired只修饰单个成员变量，不能批量作用于多个变量
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    public Map<String, Object> register(User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("success", false);
            response.put("message", "密码不能为空");
            return response;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        User savedUser = userRepository.save(user);    //UserRepository是继承自JpaRepository的接口。JpaRepository的save()方法会返回保存后的实体对象，且类型与传入的对象一致。

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());

        response.put("success", true);
        response.put("message", "注册成功");
        response.put("token", token);
        response.put("user", savedUser);

        return response;
    }

    public Map<String, Object> login(String identifier, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = findByIdentifier(identifier);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            response.put("success", true);
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("user", user);

            return response;

        } catch (BadCredentialsException e) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登录失败: 用户可能未注册" + e.getMessage());
            return response;
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByIdentifier(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 删除（注销）用户及其关联的所有数据
     */
    @Transactional
    public Map<String, Object> deleteUser(Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 检查用户是否存在
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 2. 联级删除关联数据
            // 注意顺序：先删除从属数据，最后删除用户
            noteRepository.deleteByUserId(userId);
            categoryRepository.deleteByUserId(userId);
            tagRepository.deleteByUserId(userId);

            // 3. 删除用户本身
            userRepository.delete(user);
            userRepository.flush(); // 强制刷新到数据库

            response.put("success", true);
            response.put("message", "用户注销成功");
            return response;

        } catch (Exception e) {
            // 记录详细错误日志
            System.err.println("注销用户失败, ID: " + userId + ", 原因: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "注销失败: " + e.getMessage());
            return response;
        }
    }
}
