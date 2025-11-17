package com.techup.travel_app.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ClerkJwtVerifier {
    
    @Value("${clerk.publishable.key}")
    private String clerkPublishableKey;
    
    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Verify JWT token จาก Clerk โดยใช้ JWKS (JSON Web Key Set)
     */
    public Claims verifyToken(String token) {
        try {
            // ดึง instance จาก publishable key
            // Format: pk_test_xxxxx หรือ pk_live_xxxxx
            String instance = extractInstanceFromPublishableKey();
            String jwksUrl = String.format("https://%s/.well-known/jwks.json", instance);
            
            // ดึง JWKS จาก Clerk
            JsonNode jwks = webClient.get()
                .uri(jwksUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
            
            // Parse token header เพื่อหา kid (key ID)
            String[] parts = token.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonNode header = objectMapper.readTree(headerJson);
            String kid = header.get("kid").asText();
            
            // หา key ที่ตรงกับ kid
            JsonNode key = null;
            for (JsonNode k : jwks.get("keys")) {
                if (k.get("kid").asText().equals(kid)) {
                    key = k;
                    break;
                }
            }
            
            if (key == null) {
                throw new RuntimeException("Key not found for kid: " + kid);
            }
            
            // สร้าง PublicKey จาก JWK
            PublicKey publicKey = createPublicKey(key);
            
            // Verify token
            return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
                
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }
    
    /**
     * ดึง user ID จาก token
     */
    public String getUserIdFromToken(String token) {
        Claims claims = verifyToken(token);
        return claims.getSubject(); // Clerk เก็บ user ID ใน "sub"
    }
    
    /**
     * ดึง email จาก token
     */
    public String getEmailFromToken(String token) {
        Claims claims = verifyToken(token);
        JsonNode emailNode = claims.get("email", JsonNode.class);
        if (emailNode != null && emailNode.isArray() && emailNode.size() > 0) {
            return emailNode.get(0).get("email_address").asText();
        }
        return null;
    }
    
    /**
     * สร้าง PublicKey จาก JWK
     */
    private PublicKey createPublicKey(JsonNode jwk) throws Exception {
        String n = jwk.get("n").asText();
        String e = jwk.get("e").asText();
        
        byte[] nBytes = Base64.getUrlDecoder().decode(n);
        byte[] eBytes = Base64.getUrlDecoder().decode(e);
        
        BigInteger modulus = new BigInteger(1, nBytes);
        BigInteger exponent = new BigInteger(1, eBytes);
        
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }
    
    /**
     * ดึง instance จาก publishable key หรือใช้ environment variable
     */
    private String extractInstanceFromPublishableKey() {
        // ถ้ามี CLERK_INSTANCE ใน environment variable ให้ใช้
        String instance = System.getProperty("clerk.instance");
        if (instance != null && !instance.isEmpty()) {
            return instance;
        }
        
        // หรือดึงจาก publishable key (ต้อง parse)
        // สำหรับตอนนี้ให้ใช้ environment variable
        throw new RuntimeException("Please set CLERK_INSTANCE environment variable (e.g., your-instance.clerk.accounts.dev)");
    }
}