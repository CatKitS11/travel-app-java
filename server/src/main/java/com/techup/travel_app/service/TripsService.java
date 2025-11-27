package com.techup.travel_app.service;

import com.techup.travel_app.dto.PageResponse;
import com.techup.travel_app.dto.TripsListItemResponse;
import com.techup.travel_app.dto.TripsResponse;
import com.techup.travel_app.dto.TripsRequest;
import com.techup.travel_app.entity.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TripsService {
    // แก้ไขให้รับ keyword
    PageResponse<TripsListItemResponse> getAllTrips(String keyword, Pageable pageable);
    
    TripsResponse getTripById(Long id);
    
    // New methods
    List<TripsListItemResponse> getMyTrips(User user);
    TripsResponse createTrip(TripsRequest request, User author);
    TripsResponse updateTrip(Long id, TripsRequest request, User author);
    void deleteTrip(Long id, User author);
}
