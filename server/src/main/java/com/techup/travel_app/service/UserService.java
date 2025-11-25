package com.techup.travel_app.service;

import com.techup.travel_app.entity.User;

public interface UserService {
    User syncUser(String clerkId, String email);
}

