package com.techup.travel_app.service;

import com.techup.travel_app.dto.PageResponse;
import com.techup.travel_app.dto.TripsListItemResponse;
import com.techup.travel_app.dto.TripsResponse;
import org.springframework.data.domain.Pageable;

public interface TripsService {
    // แก้ไขให้รับ keyword
    PageResponse<TripsListItemResponse> getAllTrips(String keyword, Pageable pageable);
    
    TripsResponse getTripById(Long id);
}
