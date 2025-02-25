package com.example.btl_ltw.service.Impl;

import com.example.btl_ltw.entity.FavoriteRoom;
import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.repository.FavoriteRoomRepository;
import com.example.btl_ltw.repository.RoomRepository;
import com.example.btl_ltw.repository.UserRepository;
import com.example.btl_ltw.service.FavoriteRoomService;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class FavoriteRoomServiceImpl implements FavoriteRoomService {

    @Autowired
    private FavoriteRoomRepository favoriteRoomRepository;

    @Override
    @Transactional
    public void saveFavoriteRoom(Long userId, Long roomId) {
        if (favoriteRoomRepository.findByUser_idAndRoom_id(userId, roomId) == null) {
            FavoriteRoom favoriteRoom = FavoriteRoom.builder()
                            .room_id(roomId)
                                    .user_id(userId)
                                            .build();
            
            favoriteRoomRepository.save(favoriteRoom);
        } else throw new DuplicateResourceException(roomId+"");
    }

    @Override
    public Page<Room> getFavoriteRooms(Pageable pageable, Long userId) {
        return favoriteRoomRepository.findByUser_id(pageable, userId);
    }

    @Override
    @Transactional
    public void removeFavoriteRoom(Long userId, Long roomId) {
        favoriteRoomRepository.deleteByUser_idAndRoom_id(userId, roomId);
    }
}
