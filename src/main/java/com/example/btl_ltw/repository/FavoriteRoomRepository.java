package com.example.btl_ltw.repository;

import com.example.btl_ltw.entity.FavoriteRoom;
import com.example.btl_ltw.entity.Room;
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
}
