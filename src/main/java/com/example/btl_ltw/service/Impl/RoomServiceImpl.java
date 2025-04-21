package com.example.btl_ltw.service.Impl;

import com.example.btl_ltw.common.RoomType;
import com.example.btl_ltw.common.RoomTypeConverter;
import com.example.btl_ltw.entity.Image;
import com.example.btl_ltw.entity.Room;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.model.request.room.RoomFilterDataRequest;
import com.example.btl_ltw.model.dto.*;
import com.example.btl_ltw.repository.*;
import com.example.btl_ltw.service.FileService;
import com.example.btl_ltw.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageRepository imageRepository;

    private static final String isApproval = "true";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void deleteRoomByRoomId(Long room_id) {
        imageRepository.deleteAllImagesByRoomId(room_id);
        commentRepository.deleteCommentsByRoom_id(room_id);
        appointmentRepository.deleteAppointmentByRoom_id(room_id);
        roomRepository.deleteById(room_id);
    }

    @Modifying
    @Transactional
    @Override
    public void updateRoom(RoomDto roomDto, Authentication auth, List<MultipartFile> imagesAdd, List<Long> imageIdsDel) throws ExecutionException, InterruptedException {
        Room oldroom = roomRepository.findById(roomDto.getRoom_id()).orElse(null);
        if (!oldroom.isVipStatus()) {
            oldroom.setIsApproval("false");
        }
        oldroom.setWaterPrice(roomDto.getWaterPrice());
        oldroom.setElectricityPrice(roomDto.getElectricityPrice());
        oldroom.setAddress(roomDto.getAddress());
        oldroom.setArea(roomDto.getArea());
        oldroom.setRoomType(RoomType.valueOf(roomDto.getRoomType()));

        commentRepository.deleteCommentsByRoom_id(roomDto.getRoom_id());
        appointmentRepository.deleteAppointmentByRoom_id(roomDto.getRoom_id());

        if (imageIdsDel != null) {
            for (Long id : imageIdsDel) {
                Optional<Image> image = imageRepository.findById(id);
                if (oldroom.getImage().equals(image.get().getUrl())) {
                    oldroom.setImage("");
                    break;
                }
            }
            imageRepository.deleteAllById(imageIdsDel);
        }

        if (imagesAdd != null && !imagesAdd.isEmpty()) {
            for (MultipartFile file : imagesAdd) {
                // Kiểm tra nếu file rỗng
                if (file.isEmpty()) {
                    continue;
                }

                // Nếu file không rỗng, tải lên Cloudinary
                Image image = new Image();
                image.setRoom_id(oldroom.getId());
                CompletableFuture<String> futureUrl = fileService.uploadFile(file);
                String imageUrl = futureUrl.get();
                // Tải file lên Cloudinary
                image.setUrl(imageUrl);
                imageRepository.save(image);
            }
        }

        if (oldroom.getImage().equals(null) || oldroom.getImage().equals("")) {
            List<String> images = imageRepository.findAllImagesByRoom_id(oldroom.getId());
            oldroom.setImage(images.get(0));//luu anh vao roomentity
        }
        roomRepository.save(oldroom);
    }


    @Override
    public Page<Room> getAllNormalRoomByManyContraints(RoomFilterDataRequest request, Pageable pageable) {
        Page<Room> roomPage;
        boolean vipStatus = false;
        if (request.isNull()) {
            roomPage = roomRepository.findAllByIsApprovalAndVipStatus(isApproval, vipStatus, pageable);
        } else {
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            roomPage = roomRepository.findAllByFilterConstraints(request.getPrice(), request.getAddress(), request.getArea(), roomType, vipStatus, pageable);
        }
        return roomPage;
    }

    @Override
    public Page<Room> getAllVipRoomByManyContraints(RoomFilterDataRequest request, Pageable pageable) {
        Page<Room> roomPage;
        boolean vipStatus = true;
        if (request.isNull()) {
            roomPage = roomRepository.findAllByIsApprovalAndVipStatus(isApproval, vipStatus, pageable);
        } else {
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            roomPage = roomRepository.findAllByFilterConstraints(request.getPrice(), request.getAddress(), request.getArea(), roomType, vipStatus, pageable);
        }
        return roomPage;
    }

    @Override
    public Page<Room> getAllRoomByManyContraintsAndUsername(RoomFilterDataRequest request, String username, Pageable pageable) {
        Page<Room> roomPage;
        if (request.isNull()) {
            roomPage = roomRepository.findAllByUsername(username, pageable);
        } else {
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            roomPage = roomRepository.findAllByFilterConstraintsAndUsername(request.getPrice(), request.getAddress(), request.getArea(), roomType, username, pageable);
        }
        return roomPage;
    }

    @Override
    public RoomDto getInfoRoomByRoom_Id(String room_id) {
        return RoomDto.toDto(roomRepository.findById(Long.parseLong(room_id)).orElse(null));
    }

    @Override
    public List<String> getAllImagesByRoom_Id(String room_id) {
        return imageRepository.findAllImagesByRoom_id(Long.parseLong(room_id));
    }

    @Modifying
    @Override
    @Transactional
    public void addRoom(RoomDto roomDto, List<String> images, String username) {
        Room room = RoomDto.toRoom(roomDto);
        if (!room.isVipStatus()) {
            room.setIsApproval("false");
        } else {
            room.setIsApproval("true");
        }

        Optional<User> user = userRepository.findUserByUsername(username);
        room.setUser_id(user.get().getId());
        room.setImage(images.get(0));//luu anh vao roomentity
        roomRepository.save(room);

        for (int i = 0; i < images.size(); i++) {//luu anh vao bang image
            Image image = new Image();
            image.setRoom_id(room.getId());
            String imageUrl = images.get(i);
            image.setUrl(imageUrl);
            imageRepository.save(image);
        }
    }

    @Override
    @Transactional
    public void approveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        room.setIsApproval("true");
        roomRepository.save(room);
    }

    // Không duyệt phòng
    @Override
    @Transactional
    public void disapproveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        roomRepository.deleteById(roomId);
    }

    @Override
    public List<RoomDto> getAllRoom() {
        List<Room> rooms = roomRepository.findAllRooms();
        return RoomDto.toDto(rooms);
    }

    @Override
    public void addView(long roomId) {
        roomRepository.addViews(roomId);
    }

    @Override
    public Page<Room> getAllRoomsWithManyContraintsForAdmin(RoomFilterDataRequest request, Pageable pageable) {
        Page<Room> roomPage;
        if (request.isNull() && request.emptyVip()) {
            roomPage = roomRepository.findAll(pageable);
        } else {
            request.setVip();
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            roomPage = roomRepository.findAllByFilterConstraintsForAdmin(request.getPrice(), request.getAddress(), request.getArea(), roomType, request.getIsVipBool(), pageable);
        }
        return roomPage;
    }

}
