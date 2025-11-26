package com.techup.travel_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.techup.travel_app.entity.Trips;

public interface TripsRepository extends JpaRepository<Trips, Long> {
    
    // ค้นหาด้วย Title หรือ Description หรือ Tags (ที่ map เป็น Text ใน DB)
    // เนื่องจาก tags เป็น Array อาจจะต้องใช้ Native Query หรือวิธีเฉพาะ
    // แต่เบื้องต้นเอา Title กับ Description ก่อนก็ได้ครับตาม Requirement
    
    @Query(value = "SELECT * FROM trips t WHERE " +
           "t.title ILIKE %:keyword% OR " +
           "t.description ILIKE %:keyword% OR " +
           ":keyword ILIKE ANY(t.tags)", 
           nativeQuery = true)
    Page<Trips> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // หรือถ้าจะใช้ tags (ที่เป็น String array) อาจจะยากหน่อยใน JPQL
    // ถ้าเอาแบบง่ายสุดตามโจทย์: "name or province"
    // ถ้า province คุณอยู่ใน tags ก็อาจจะต้องเขียน Native Query
}
