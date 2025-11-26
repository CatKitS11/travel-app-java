package com.techup.travel_app.controller;

import com.techup.travel_app.dto.PageResponse;
import com.techup.travel_app.dto.TripsListItemResponse;
import com.techup.travel_app.dto.TripsResponse;
import com.techup.travel_app.service.TripsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // อนุญาตให้ frontend เรียกได้
public class TripsController {
    
    private final TripsService tripsService;
    
    @GetMapping
    public ResponseEntity<PageResponse<TripsListItemResponse>> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "created_at") String sortBy, // EDIT: เปลี่ยนเป็น created_at
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String keyword) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<TripsListItemResponse> response = tripsService.getAllTrips(keyword, pageable);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TripsResponse> getTripById(@PathVariable Long id) {
        TripsResponse response = tripsService.getTripById(id);
        return ResponseEntity.ok(response);
    }
}
