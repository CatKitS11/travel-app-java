package com.techup.travel_app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techup.travel_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestBody String payload) {
        // NOTE: In production, verify the 'svix-signature', 'svix-id', and 'svix-timestamp' headers!
        // Documentation: https://clerk.com/docs/integrations/webhooks/overview#validate-the-request
        
        try {
            JsonNode root = objectMapper.readTree(payload);
            String type = root.path("type").asText();
            
            if ("user.deleted".equals(type)) {
                String deletedUserId = root.path("data").path("id").asText();
                log.info("Received user.deleted webhook for ID: {}", deletedUserId);
                
                if (deletedUserId != null && !deletedUserId.isEmpty()) {
                    userService.deleteUserByClerkId(deletedUserId);
                }
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.badRequest().build();
        }
    }
}