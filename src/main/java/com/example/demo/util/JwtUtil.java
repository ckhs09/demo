package com.example.demo.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
 
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
@Slf4j
@Component
public class JwtUtil {
   
   @Value("${jwt.secret}")
   private String secret;
   
   @Value("${jwt.expiration}")
   private long expirationTime;
   
   private Key getSigningKey() {
       byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
       return Keys.hmacShaKeyFor(keyBytes);
   }
    public String generateToken(String studentId) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] 开始生成token, studentId: {}", requestId, studentId);
       
       Date now = new Date();
       Date expiration = new Date(System.currentTimeMillis() + expirationTime);
       
       try {
           String token = Jwts.builder()
                   .setSubject(studentId)
                   .setIssuedAt(now)
                   .setExpiration(expiration)
                   .signWith(getSigningKey())
                   .compact();
                   
           log.info("[{}] Token生成成功, 过期时间: {}", requestId, expiration);
           return token;
       } catch (Exception e) {
           log.error("[{}] Token生成失败: {}", requestId, e.getMessage());
           throw e;
       }
   }
    public String getStudentIdFromToken(String token) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] 开始解析token", requestId);
       
       try {
           Claims claims = Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
                   
           String studentId = claims.getSubject();
           Date expiration = claims.getExpiration();
           
           log.info("[{}] Token解析成功 - studentId: {}, 过期时间: {}", 
                   requestId, studentId, expiration);
           return studentId;
           
       } catch (ExpiredJwtException e) {
           log.error("[{}] Token已过期: {}", requestId, e.getMessage());
           throw e;
       } catch (Exception e) {
           log.error("[{}] Token解析失败: {}", requestId, e.getMessage());
           throw e;
       }
   }
    public boolean validateToken(String token) {
       String requestId = UUID.randomUUID().toString().substring(0, 8);
       log.info("[{}] 开始验证token", requestId);
       
       try {
           Claims claims = Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
                   
           Date expiration = claims.getExpiration();
           Date now = new Date();
           
           boolean isValid = !expiration.before(now);
           log.info("[{}] Token验证结果: {}, 过期时间: {}", requestId, isValid, expiration);
           
           return isValid;
           
       } catch (ExpiredJwtException e) {
           log.error("[{}] Token已过期", requestId);
           return false;
       } catch (Exception e) {
           log.error("[{}] Token验证失败: {}", requestId, e.getMessage());
           return false;
       }
   }
   
   public boolean isTokenExpired(String token) {
       try {
           Claims claims = Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
                   
           return claims.getExpiration().before(new Date());
       } catch (ExpiredJwtException e) {
           return true;
       } catch (Exception e) {
           return true;
       }
   }
   
   public Date getExpirationDateFromToken(String token) {
       try {
           Claims claims = Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
                   
           return claims.getExpiration();
       } catch (Exception e) {
           return null;
       }
   }
}

