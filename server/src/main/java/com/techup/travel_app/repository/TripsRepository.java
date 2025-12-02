package com.techup.travel_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.techup.travel_app.entity.Trips;
import com.techup.travel_app.entity.User;
import java.util.List;

public interface TripsRepository extends JpaRepository<Trips, Long> {
    
    // ค้นหาด้วย Title หรือ Description หรือ Tags (ที่ map เป็น Text ใน DB)
    // เนื่องจาก tags เป็น Array อาจจะต้องใช้ Native Query หรือวิธีเฉพาะ
    // แต่เบื้องต้นเอา Title กับ Description ก่อนก็ได้ครับตาม Requirement
    
    @Query(value = "SELECT * FROM trips t WHERE " +
           "t.title ILIKE CONCAT('%', :keyword, '%') OR " +
           "t.description ILIKE CONCAT('%', :keyword, '%') OR " +
           "array_to_string(t.tags, ',') ILIKE CONCAT('%', :keyword, '%')",
           countQuery = "SELECT count(*) FROM trips t WHERE " +
           "t.title ILIKE CONCAT('%', :keyword, '%') OR " +
           "t.description ILIKE CONCAT('%', :keyword, '%') OR " +
           "array_to_string(t.tags, ',') ILIKE CONCAT('%', :keyword, '%')",
           nativeQuery = true)
    Page<Trips> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // หรือถ้าจะใช้ tags (ที่เป็น String array) อาจจะยากหน่อยใน JPQL
    // ถ้าเอาแบบง่ายสุดตามโจทย์: "name or province"
    // ถ้า province คุณอยู่ใน tags ก็อาจจะต้องเขียน Native Query

    // New method for finding user's trips
    List<Trips> findAllByAuthorOrderByUpdatedAtDesc(User author);

    void deleteByAuthor(User author);
}
