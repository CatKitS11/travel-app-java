package com.techup.travel_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity // บอก Spring ว่าคลาสนี้จะถูก map เป็นตารางใน Database
@Table(name = "users") // ระบุชื่อตารางเป็น "users" เพื่อหลีกเลี่ยง reserved keyword "user"
@Data // สร้าง getter/setter/toString/equals/hashCode ให้อัตโนมัติ
@NoArgsConstructor // Constructor ว่าง (จำเป็นสำหรับ JPA)
@AllArgsConstructor // Constructor ที่มีทุก field
@Builder // ใช้สร้าง object แบบ chain

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    @Column(name = "id")
    private Long id; // Primary key

    @Column(name = "clerk_id", unique = true, nullable = true) // nullable = true เพราะอาจยังไม่มีตอนสร้าง)
    private String clerkId; // เก็บ Clerk User ID

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    // @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    // private String passwordHash;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;
}
