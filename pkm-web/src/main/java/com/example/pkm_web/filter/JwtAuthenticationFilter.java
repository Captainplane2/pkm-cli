package com.example.pkm_web.filter;

import com.example.pkm_web.model.User;
import com.example.pkm_web.service.UserAuthService;
import com.example.pkm_web.util.JwtUtil;
import com.example.pkm_web.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器，继承OncePerRequestFilter确保每个请求只被过滤一次
 * 用于验证请求中的JWT令牌并设置安全上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UserAuthService userAuthService;

    /**
     * 内部过滤方法，处理JWT令牌验证和用户认证
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 每次请求开始前清理之前的上下文
        UserContext.clear();

        // 从请求头中提取Authorization头部信息
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 检查授权头部是否存在且以指定前缀开头，提取令牌
        if (authHeader != null && authHeader.startsWith(jwtUtil.getTokenPrefix() + " ")) {
            token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token != null) {
                username = jwtUtil.extractUsername(token);
            }
        }

        // 如果用户名存在且安全上下文中没有认证信息，则进行用户认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userAuthService.findByUsername(username);

            // 验证用户存在且令牌有效后，创建认证令牌并设置到安全上下文
            if (user != null && jwtUtil.validateToken(token, username)) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles("USER")
                        .build();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // 将用户ID存入 UserContext (ThreadLocal)
                UserContext.setCurrentUserId(user.getId());
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清理 ThreadLocal 防止内存泄漏
            UserContext.clear();
        }
    }

    /**
     * 判断请求是否不应被过滤
     * @param request HTTP请求对象
     * @return 如果请求路径以/api/auth/或/api/test/开头则返回true，表示不应过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 注册和登录不需要过滤，但注销需要获取上下文，所以不能排除
        return path.equals("/api/auth/register") || path.equals("/api/auth/login") || path.startsWith("/api/test/");
    }
}
