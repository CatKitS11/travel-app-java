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
            // 1. Decode Token Payload เพื่อหา Issuer (iss)
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new RuntimeException("Invalid JWT token format");
            }
            
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payload = objectMapper.readTree(payloadJson);
            
            if (!payload.has("iss")) {
                 throw new RuntimeException("Token missing 'iss' claim");
            }
            
            String issuer = payload.get("iss").asText();
            String jwksUrl = issuer + "/.well-known/jwks.json";
            
            // 2. ดึง JWKS จาก Clerk
            JsonNode jwks = webClient.get()
                .uri(jwksUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
            
            // 3. Parse token header เพื่อหา kid (key ID)
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonNode header = objectMapper.readTree(headerJson);
            String kid = header.get("kid").asText();
            
            // 4. หา key ที่ตรงกับ kid
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
            
            // 5. สร้าง PublicKey จาก JWK
            PublicKey publicKey = createPublicKey(key);
            
            // 6. Verify token
            return Jwts.parser()
                .verifyWith(publicKey)
                .clockSkewSeconds(3600) // ยอมให้เวลาต่างกันได้ 1 ชั่วโมง! (Token เก่าก็ยังใช้ได้)
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
        try {
            JsonNode emailNode = claims.get("email", JsonNode.class);
            if (emailNode != null && emailNode.isArray() && emailNode.size() > 0) {
                return emailNode.get(0).get("email_address").asText();
            }
        } catch (Exception e) {
            // ignore
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
}
