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
import com.techup.travel_app.dto.TripsRequest;
import com.techup.travel_app.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpMethod;
import java.util.Map;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
            @RequestParam(defaultValue = "createdAt") String sortBy,
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

    @GetMapping("/mine")
    public ResponseEntity<List<TripsListItemResponse>> getMyTrips(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tripsService.getMyTrips(user));
    }
    
    @PostMapping
    public ResponseEntity<TripsResponse> createTrip(
            @Valid @RequestBody TripsRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tripsService.createTrip(request, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TripsResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripsRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tripsService.updateTrip(id, request, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        tripsService.deleteTrip(id, user);
        return ResponseEntity.noContent().build();
    }

    // เพิ่ม Endpoint นี้
    @PostMapping("/{id}/photos")
    public ResponseEntity<TripsResponse> addPhotoToTrip(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload, // รับเป็น JSON { "url": "..." }
            @AuthenticationPrincipal User user) {
            
        String photoUrl = payload.get("url");
        if (photoUrl == null || photoUrl.isBlank()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL is required");
        }

        return ResponseEntity.ok(tripsService.addPhotoToTrip(id, photoUrl, user));
    }
}
