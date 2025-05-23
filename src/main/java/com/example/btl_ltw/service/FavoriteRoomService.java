package com.example.btl_ltw.service;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteRoomService {
    void saveFavoriteRoom(Long userId, Long roomId);
    Page<Room> getFavoriteRoomsByManyContraints(RoomFilterDataRequest request, Pageable pageable, Long userId);

    void removeFavoriteRoom(Long userId, Long roomId);
}
