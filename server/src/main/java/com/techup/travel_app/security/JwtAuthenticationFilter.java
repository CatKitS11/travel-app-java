package com.techup.travel_app.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final ClerkJwtVerifier clerkJwtVerifier;
    
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
                
                // สร้าง Authentication object
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userId, // principal = Clerk User ID
                        null,   // credentials
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                
                // เก็บข้อมูลเพิ่มเติมใน details
                authentication.setDetails(email);
                
                // ตั้งค่า Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (Exception e) {
                // Token ไม่ถูกต้อง - ไม่ต้องทำอะไร (จะถูก reject โดย SecurityConfig)
                logger.warn("JWT verification failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}