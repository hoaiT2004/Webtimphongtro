package com.example.btl_ltw.repository;

import com.example.btl_ltw.entity.RoomStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RoomStarRepository extends JpaRepository<RoomStar, Long> {

    RoomStar findRoomStarByRoomIdAndUsername(long roomId, String username);

    @Query("UPDATE RoomStar r SET r.rating = :rating WHERE r.roomId = :roomId AND r.username = :username")
    @Modifying
    @Transactional
    void updateRoomStarByRoomIdAndUsername(long roomId, String username, long rating);

    @Query("SELECT COUNT(r) FROM RoomStar r WHERE r.roomId = :roomId AND r.rating = :rating")
    int StarTotal(long roomId, long rating);
}
