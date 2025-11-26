package com.techup.travel_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripsListItemResponse {
    private Long id;
    private String title;
    private String coverImage; // photos[0] หรือ null
    private String[] photos;   // EDIT: เพิ่ม field นี้
    private String province; // ดึงจาก tags หรือ null
    private String shortDescription; // description.substring(0, 120) หรือ description
}