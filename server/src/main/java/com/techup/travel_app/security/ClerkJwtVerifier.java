package com.techup.travel_app.security;

import com.clerk.backendapi.ClerkClient;
import com.clerk.backendapi.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClerkJwtVerifier {
    
    @Value("${clerk.secret.key}")
    private String clerkSecretKey;
    
    private ClerkClient clerkClient;
    
    /**
     * Initialize Clerk Client (เรียกครั้งเดียว)
     */
    private ClerkClient getClerkClient() {
        if (clerkClient == null) {
            clerkClient = new ClerkClient(clerkSecretKey);
        }
        return clerkClient;
    }
    
    /**
     * Verify JWT token จาก Clerk
     * @param token JWT token จาก Authorization header
     * @return Clerk User object ถ้า token ถูกต้อง
     * @throws Exception ถ้า token ไม่ถูกต้อง
     */
    public User verifyToken(String token) {
        try {
            return getClerkClient().verifyToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }
    
    /**
     * ดึง user ID จาก token
     */
    public String getUserIdFromToken(String token) {
        User user = verifyToken(token);
        return user.getId();
    }
    
    /**
     * ดึง email จาก token
     */
    public String getEmailFromToken(String token) {
        User user = verifyToken(token);
        return user.getEmailAddresses().get(0).getEmailAddress();
    }
    
    /**
     * ดึง Clerk User object ทั้งหมด
     */
    public User getClerkUser(String token) {
        return verifyToken(token);
    }
}