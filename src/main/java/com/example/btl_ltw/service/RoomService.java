package com.example.btl_ltw.service;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.model.dto.RoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {

    Page<Room> getAllRoomByManyContraints(RoomFilterDataRequest request, Pageable pageable);

    RoomDto getInfoRoomByRoom_Id(String room_id);

    List<String> getAllImagesByRoom_Id(String room_id);

    void addRoom(RoomDto roomDto, List<MultipartFile> images, Authentication auth);

    void deleteRoomByRoomId(Long room_id);

    Page<Room> getRoomsByUser(String isApproval, String username, Pageable pageable);

    Page<Room> getAllRoomsByUser(String username, Pageable pageable);

    void updateRoom(RoomDto roomDto, Authentication auth, List<MultipartFile> imagesAdd, List<Long> imageIdsDel);

    Page<Room> getAllRoomsForAdmin(Pageable pageable);

    void approveRoom(Long roomId);

    void disapproveRoom(Long roomId);

    List<RoomDto> getAllRoom();
}
