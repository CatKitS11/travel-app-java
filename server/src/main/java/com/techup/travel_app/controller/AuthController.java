package com.techup.travel_app.controller;

import com.techup.travel_app.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(@AuthenticationPrincipal User user) {
        // ถ้าเข้ามาถึงตรงนี้ได้ แสดงว่า JwtAuthenticationFilter ทำงานผ่าน
        // และ User ถูก Sync ลง DB เรียบร้อยแล้ว
        return ResponseEntity.ok().body("User synced successfully: " + (user != null ? user.getEmail() : "Unknown"));
    }
}

