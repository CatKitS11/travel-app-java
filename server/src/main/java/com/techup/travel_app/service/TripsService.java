package com.techup.travel_app.service;

import com.techup.travel_app.dto.PageResponse;
import com.techup.travel_app.dto.TripsListItemResponse;
import com.techup.travel_app.dto.TripsResponse;
import org.springframework.data.domain.Pageable;

public interface TripsService {
    PageResponse<TripsListItemResponse> getAllTrips(Pageable pageable);
    TripsResponse getTripById(Long id);
}
