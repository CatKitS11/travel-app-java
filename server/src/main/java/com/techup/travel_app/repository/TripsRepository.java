package com.techup.travel_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.techup.travel_app.entity.Trips;

public interface TripsRepository extends JpaRepository<Trips, Long> {
    
}
