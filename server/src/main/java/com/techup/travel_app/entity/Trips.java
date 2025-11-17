package com.techup.travel_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trips {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // @ElementCollection
    // @CollectionTable(name = "trip_photos", joinColumns = @JoinColumn(name = "trip_id"))
    // @Column(name = "photo")
    // @Builder.Default
    // private List<String> photos = new ArrayList<>();
    @JdbcTypeCode(SqlTypes.ARRAY)                                     
    @Column(name = "photos", columnDefinition = "text[]")             
    @Builder.Default                                                  
    private String[] photos = new String[0];  
    
    // @ElementCollection
    // @CollectionTable(name = "trip_tags", joinColumns = @JoinColumn(name = "trip_id"))
    // @Column(name = "tag")
    // @Builder.Default
    // private List<String> tags = new ArrayList<>();
    @JdbcTypeCode(SqlTypes.ARRAY)                                     
    @Column(name = "tags", columnDefinition = "text[]")               
    @Builder.Default                                                  
    private String[] tags = new String[0];
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_trip_author"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User author;
    
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}

