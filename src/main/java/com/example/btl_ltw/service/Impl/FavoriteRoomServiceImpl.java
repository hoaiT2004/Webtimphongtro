package com.example.btl_ltw.service.Impl;

import com.example.btl_ltw.common.RoomType;
import com.example.btl_ltw.common.RoomTypeConverter;
import com.example.btl_ltw.entity.FavoriteRoom;
import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.repository.FavoriteRoomRepository;
import com.example.btl_ltw.service.FavoriteRoomService;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteRoomServiceImpl implements FavoriteRoomService {

    @Autowired
    private FavoriteRoomRepository favoriteRoomRepository;

    public Integer getTotalAddFavoriteByRoomId(Long roomId) {
        return favoriteRoomRepository.getTotalAddFavoriteByRoomId(roomId);
    }

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
    public Page<Room> getFavoriteRoomsByManyContraints(RoomFilterDataRequest request, Pageable pageable, Long userId) {
        Page<Room> pages = null;
        if (request.isNull()) {
            pages = favoriteRoomRepository.findByUser_id(pageable, userId);
        } else {
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            pages = favoriteRoomRepository.findAllWithManyConstraints(request.getPrice(), request.getAddress(), request.getArea(), roomType, userId, pageable);
        }
        return pages;
    }

    @Override
    @Transactional
    public void removeFavoriteRoom(Long userId, Long roomId) {
        favoriteRoomRepository.deleteByUser_idAndRoom_id(userId, roomId);
    }
}
