package com.techup.travel_app.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.techup.travel_app.entity.User;
import com.techup.travel_app.service.UserService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final ClerkJwtVerifier clerkJwtVerifier;
    private final UserService userService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // Verify token กับ Clerk
                Claims claims = clerkJwtVerifier.verifyToken(token);
                String userId = clerkJwtVerifier.getUserIdFromToken(token);
                String email = clerkJwtVerifier.getEmailFromToken(token);
                
                log.debug("JWT verified - UserId: {}, Email: {}", userId, email);
                
                // Sync user to database (via Service to handle transaction correctly)
                User user = userService.syncUser(userId, email);
                
                log.debug("User synced - ID: {}, ClerkId: {}, Email: {}", 
                    user.getId(), user.getClerkId(), user.getEmail());
                
                // สร้าง Authentication object โดยใช้ User entity เป็น principal
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        user, // principal เป็น User entity เพื่อให้ Controller เรียกใช้ได้ง่าย
                        null,   // credentials
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                
                // เก็บข้อมูลเพิ่มเติมใน details
                authentication.setDetails(email);
                
                // ตั้งค่า Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (Exception e) {
                // Token ไม่ถูกต้อง - ไม่ต้องทำอะไร (จะถูก reject โดย SecurityConfig)
                log.error("JWT verification or user sync failed: " + e.getMessage(), e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
