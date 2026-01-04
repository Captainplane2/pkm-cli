package com.example.pkm_web.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil 类用于处理 JWT（JSON Web Token令牌）的生成、解析和验证等操作。
 * 提供了令牌的创建、解析、验证以及从HTTP头部提取令牌等功能。
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Value("${jwt.header-name}")
    private String headerName;

    /**
     * 获取JWT签名密钥
     * @return SecretKey 签名密钥对象
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从JWT令牌中提取用户名
     * @param token JWT令牌字符串
     * @return String 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中提取过期时间
     * @param token JWT令牌字符串
     * @return Date 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从JWT令牌中提取指定的声明信息
     * @param token JWT令牌字符串
     * @param claimsResolver 声明解析函数
     * @param <T> 返回值类型
     * @return T 解析后的声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析JWT令牌并获取所有声明信息
     * @param token JWT令牌字符串
     * @return Claims 包含所有声明信息的对象
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查JWT令牌是否已过期
     * @param token JWT令牌字符串
     * @return Boolean 如果令牌已过期返回true，否则返回false
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 根据用户名生成JWT令牌
     * @param username 用户名
     * @return String 生成的JWT令牌
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * 根据用户名和角色生成JWT令牌
     * @param username 用户名
     * @param role 用户角色
     * @return String 生成的JWT令牌
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * 创建JWT令牌
     * @param claims 令牌中的声明信息
     * @param subject 令牌的主题（通常是用户名）
     * @return String 生成的JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证JWT令牌的有效性
     * @param token JWT令牌字符串
     * @param username 预期的用户名
     * @return Boolean 如果令牌有效且用户名匹配返回true，否则返回false
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 验证JWT令牌的有效性（不验证用户名）
     * @param token JWT令牌字符串
     * @return Boolean 如果令牌有效返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取令牌前缀
     * @return String 令牌前缀字符串
     */
    public String getTokenPrefix() {
        return tokenPrefix;
    }

    /**
     * 获取HTTP头部名称
     * @return String HTTP头部名称
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * 获取令牌过期时间
     * @return Long 过期时间（毫秒）
     */
    public Long getExpiration() {
        return expiration;
    }

    /**
     * 从HTTP认证头部中提取JWT令牌
     * @param authHeader HTTP认证头部字符串
     * @return String 提取的JWT令牌，如果格式不正确则返回null
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(tokenPrefix + " ")) {
            return authHeader.substring(tokenPrefix.length() + 1);
        }
        return null;
    }
}