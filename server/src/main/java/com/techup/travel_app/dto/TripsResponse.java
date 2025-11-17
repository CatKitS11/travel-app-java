package com.techup.travel_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripsResponse {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private String[] photos;
    
    private String[] tags;
    
    private Double latitude;
    
    private Double longitude;
    
    // ข้อมูลผู้สร้าง (เลือกแบบใดแบบหนึ่ง)
    // แบบ 1: แค่ ID
    private Long authorId;
    
    // แบบ 2: ข้อมูลครบ (แนะนำ)
    private AuthorInfo author;
    
    private OffsetDateTime createdAt;
    
    private OffsetDateTime updatedAt;
    
    // Inner class สำหรับข้อมูล author
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorInfo {
        private Long id;
        private String email;
        private String displayName;
    }
}