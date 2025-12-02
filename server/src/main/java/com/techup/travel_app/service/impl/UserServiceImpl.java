package com.techup.travel_app.service.impl;

import com.techup.travel_app.entity.User;
import com.techup.travel_app.repository.TripsRepository;
import com.techup.travel_app.repository.UserRepository;
import com.techup.travel_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TripsRepository tripsRepository;

    @Override
    @Transactional
    public User syncUser(String userId, String email) {
        // Sync user to database
        User user = userRepository.findByClerkId(userId)
            .orElseGet(() -> {
                log.debug("User not found by clerkId: {}, checking by email...", userId);
                // ตรวจสอบว่ามี email นี้อยู่แล้วหรือไม่ (กรณี migrate หรือ error state)
                if (email != null) {
                     return userRepository.findByEmail(email)
                        .map(existingUser -> {
                            log.debug("Found existing user by email, updating clerkId");
                            existingUser.setClerkId(userId);
                            return userRepository.save(existingUser);
                        })
                        .orElseGet(() -> {
                            log.debug("Creating new user with email: {}", email);
                            User newUser = User.builder()
                                .clerkId(userId)
                                .email(email)
                                .displayName(email)
                                .passwordHash("CLERK_AUTH") // ใส่ค่าหลอกๆ ไป
                                .build();
                            User saved = userRepository.save(newUser);
                            log.info("New user created - ID: {}, ClerkId: {}, Email: {}", 
                                saved.getId(), saved.getClerkId(), saved.getEmail());
                            return saved;
                        });
                } else {
                    // กรณีไม่มี email ใน token (ซึ่งไม่ควรเกิดขึ้นถ้าระบุ scope ถูกต้อง)
                    log.warn("No email in token for userId: {}", userId);
                    User newUser = User.builder()
                        .clerkId(userId)
                        .email("no-email-" + userId) // Fallback
                        .displayName("User " + userId)
                        .passwordHash("CLERK_AUTH") // ใส่ค่าหลอกๆ ไป
                        .build();
                    User saved = userRepository.save(newUser);
                    log.info("New user created without email - ID: {}, ClerkId: {}", 
                        saved.getId(), saved.getClerkId());
                    return saved;
                }
            });

        // อัพเดท email ถ้าเปลี่ยน (Optional)
        if (email != null && !email.equals(user.getEmail())) {
            log.debug("Updating email for user ID: {} from {} to {}", 
                user.getId(), user.getEmail(), email);
            user.setEmail(email);
            user = userRepository.save(user);
        }
        
        return user;
    }

    @Override
    @Transactional
    public void deleteUserByClerkId(String clerkId) {
        userRepository.findByClerkId(clerkId).ifPresent(user -> {
            log.info("Deleting user: {} and their trips", user.getEmail());
            tripsRepository.deleteByAuthor(user);
            userRepository.delete(user);
        });
    }
}