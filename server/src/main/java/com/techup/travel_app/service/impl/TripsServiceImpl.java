package com.techup.travel_app.service.impl;

import com.techup.travel_app.dto.PageResponse;
import com.techup.travel_app.dto.TripsListItemResponse;
import com.techup.travel_app.dto.TripsResponse;
import com.techup.travel_app.entity.Trips;
import com.techup.travel_app.exception.ResourceNotFoundException;
import com.techup.travel_app.repository.TripsRepository;
import com.techup.travel_app.service.TripsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripsServiceImpl implements TripsService {
    
    private final TripsRepository tripsRepository;
    
    @Override
    public PageResponse<TripsListItemResponse> getAllTrips(String keyword, Pageable pageable) {
        Page<Trips> tripsPage;
        
        // เช็คว่ามี keyword ไหม
        if (keyword != null && !keyword.trim().isEmpty()) {
            
            // --- FIX SORTING ISSUE FOR NATIVE QUERY ---
            // สร้าง Sort ใหม่โดยแปลงชื่อ field ให้ตรงกับ DB
            Sort newSort = Sort.unsorted();
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                // แปลงชื่อที่ frontend ส่งมา (createdAt) ให้เป็นชื่อใน DB (created_at)
                if (property.equals("createdAt")) property = "created_at";
                if (property.equals("updatedAt")) property = "updated_at";
                // เพิ่มการแปลง field อื่นๆ ถ้าจำเป็น เช่น title -> title
                
                Sort.Order newOrder = order.isAscending() 
                    ? Sort.Order.asc(property) 
                    : Sort.Order.desc(property);
                
                newSort = newSort.isSorted() ? newSort.and(Sort.by(newOrder)) : Sort.by(newOrder);
            }
            
            // สร้าง Pageable ใหม่
            Pageable nativePageable = PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                newSort
            );
            // -------------------------------------------

            // ส่ง nativePageable แทน pageable เดิม
            tripsPage = tripsRepository.findByKeyword(keyword, nativePageable);
        } else {
            // ถ้าไม่มี keyword เรียก findAll (ซึ่งเป็น JPQL/Hibernate อัตโนมัติ ไม่ต้องแปลงชื่อ)
            tripsPage = tripsRepository.findAll(pageable);
        }
        
        List<TripsListItemResponse> content = tripsPage.getContent().stream()
            .map(this::mapToListItemResponse)
            .collect(Collectors.toList());
        
        return PageResponse.<TripsListItemResponse>builder()
            .content(content)
            .page(tripsPage.getNumber())
            .size(tripsPage.getSize())
            .totalElements(tripsPage.getTotalElements())
            .totalPages(tripsPage.getTotalPages())
            .hasNext(tripsPage.hasNext())
            .hasPrevious(tripsPage.hasPrevious())
            .build();
    }
    
    @Override
    public TripsResponse getTripById(Long id) {
        Trips trip = tripsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        return mapToResponse(trip);
    }
    
    private TripsListItemResponse mapToListItemResponse(Trips trip) {
        // ดึง cover image (รูปแรก)
        String coverImage = (trip.getPhotos() != null && trip.getPhotos().length > 0) 
            ? trip.getPhotos()[0] 
            : null;
        
        // ดึง province จาก tags (หา tag ที่เป็นชื่อจังหวัด)
        String province = extractProvinceFromTags(trip.getTags());
        
        // สร้าง short description (max 120 chars)
        String shortDescription = trip.getDescription();
        if (shortDescription != null && shortDescription.length() > 120) {
            shortDescription = shortDescription.substring(0, 120) + "...";
        }
        
        return TripsListItemResponse.builder()
            .id(trip.getId())
            .title(trip.getTitle())
            .coverImage(coverImage)
            .photos(trip.getPhotos()) // EDIT: ส่ง photos array ไปด้วย
            .province(province)
            .shortDescription(shortDescription)
            .tags(trip.getTags())
            .latitude(trip.getLatitude())
            .longitude(trip.getLongitude())
            // .authorId(trip.getAuthor() != null ? trip.getAuthor().getId() : null)
            // .createdAt(trip.getCreatedAt())
            // .updatedAt(trip.getUpdatedAt())
            .build();
    }
    
    private TripsResponse mapToResponse(Trips trip) {
        TripsResponse.AuthorInfo authorInfo = null;
        if (trip.getAuthor() != null) {
            authorInfo = TripsResponse.AuthorInfo.builder()
                .id(trip.getAuthor().getId())
                .email(trip.getAuthor().getEmail())
                .displayName(trip.getAuthor().getDisplayName())
                .build();
        }
        
        return TripsResponse.builder()
            .id(trip.getId())
            .title(trip.getTitle())
            .description(trip.getDescription())
            .photos(trip.getPhotos())
            .tags(trip.getTags())
            .latitude(trip.getLatitude())
            .longitude(trip.getLongitude())
            .authorId(trip.getAuthor() != null ? trip.getAuthor().getId() : null)
            // .author(authorInfo)
            .createdAt(trip.getCreatedAt())
            .updatedAt(trip.getUpdatedAt())
            .build();
    }
    
    // Helper method: ดึง province จาก tags
    // ถ้า tags มี "ตราด", "กรุงเทพ", "เชียงใหม่" ฯลฯ จะ return ค่านั้น
    private String extractProvinceFromTags(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }
        
        // List ของจังหวัดไทย (อาจจะต้องเพิ่มให้ครบ)
        List<String> provinces = Arrays.asList(
            "กรุงเทพ", "เชียงใหม่", "เชียงราย", "ตราด", "ภูเก็ต", 
            "พัทยา", "หัวหิน", "เกาะสมุย", "เกาะช้าง", "เกาะพีพี"
        );
        
        return Arrays.stream(tags)
            .filter(tag -> provinces.stream()
                .anyMatch(province -> tag.contains(province) || province.contains(tag)))
            .findFirst()
            .orElse(null);
    }
}
