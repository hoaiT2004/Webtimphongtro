package com.example.btl_ltw.repository;

import com.example.btl_ltw.common.RoomType;
import com.example.btl_ltw.entity.FavoriteRoom;
import com.example.btl_ltw.entity.Room;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface FavoriteRoomRepository extends JpaRepository<FavoriteRoom, Long> {

    @Query("SELECT r FROM FavoriteRoom AS r " +
            "WHERE r.user_id = :userId AND r.room_id = :roomId")
    FavoriteRoom findByUser_idAndRoom_id(Long userId, Long roomId);

    @Query("SELECT r1 FROM FavoriteRoom AS r " +
            "INNER JOIN Room AS r1 " +
            "ON r1.id = r.room_id " +
            "WHERE r.user_id = :userId " +
            "ORDER BY r.createdAt DESC")
    Page<Room> findByUser_id(Pageable pageable, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FavoriteRoom AS r " +
            "WHERE r.user_id = :userId AND r.room_id = :roomId")
    void deleteByUser_idAndRoom_id(Long userId, Long roomId);

    @Query("SELECT r FROM FavoriteRoom AS r1 " +
            "INNER JOIN Room AS r ON r.id = r1.id " +
            "WHERE r1.user_id = :userId AND " +
            "(:price = '' OR " +
            "(:price = '1' AND r.price < 1) OR " +
            "(:price = '1-2' AND r.price >= 1 AND r.price <= 2) OR " +
            "(:price = '2-3' AND r.price >= 2 AND r.price <= 3) OR " +
            "(:price = '3' AND r.price > 3)" +
            ")" +
            " AND (:address = '' OR r.address LIKE CONCAT('%', :address, '%'))" +
            " AND (:area = '' OR" +
            "(:area = '20' AND r.area < 20) OR" +
            "(:area = '20-30' AND r.area >= 20 AND r.area <= 30) OR" +
            "(:area = '30-40' AND r.area >= 30 AND r.area <= 40) OR" +
            "(:area = '40' AND r.area > 40)) "+
            " AND (:roomType IS NULL OR r.roomType = :roomType)")
    Page<Room> findAllWithManyConstraints(@Param("price") String price, @Param("address") String address, @Param("area") String area, @Param("roomType") RoomType roomType, Long userId, Pageable pageable);

    @Query("SELECT COUNT(*) FROM FavoriteRoom f " +
            "WHERE f.room_id = :roomId")
    Integer getTotalAddFavoriteByRoomId(Long roomId);
}
