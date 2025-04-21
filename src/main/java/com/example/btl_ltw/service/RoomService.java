package com.example.btl_ltw.service;

import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.model.dto.RoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface RoomService {

    Page<Room> getAllNormalRoomByManyContraints(RoomFilterDataRequest request, Pageable pageable);

    Page<Room> getAllVipRoomByManyContraints(RoomFilterDataRequest request, Pageable pageable);

    Page<Room> getAllRoomByManyContraintsAndUsername(RoomFilterDataRequest request, String username, Pageable pageable);

    RoomDto getInfoRoomByRoom_Id(String room_id);

    List<String> getAllImagesByRoom_Id(String room_id);

    void addRoom(RoomDto roomDto, List<String> images, String username);

    void deleteRoomByRoomId(Long room_id);

    void updateRoom(RoomDto roomDto, Authentication auth, List<MultipartFile> imagesAdd, List<Long> imageIdsDel) throws ExecutionException, InterruptedException;

    void approveRoom(Long roomId);

    void disapproveRoom(Long roomId);

    List<RoomDto> getAllRoom();

    void addView(long roomId);

    Page<Room> getAllRoomsWithManyContraintsForAdmin(RoomFilterDataRequest request, Pageable pageable);
}
