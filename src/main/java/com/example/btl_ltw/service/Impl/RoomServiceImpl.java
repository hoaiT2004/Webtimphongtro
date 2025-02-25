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

//    @Override
//    public List<RoomDto> getAllRoomByUser(String username) {
//        Optional<User> user = userRepository.findUserByUsername(username);
//        List<Room> rooms = roomRepository.findAllByUserid(user.get().getId());
//        List<RoomDto> roomDtos = new ArrayList<>();
//        for (Room room : rooms) {
//            RoomDto roomDto = RoomDto.toDto(room);
//            roomDtos.add(roomDto);
//        }
//        return roomDtos;
//    }

    @Override
    public void deleteRoomByRoomId(Long room_id) {
        imageRepository.deleteAllImagesByRoomId(room_id);
        commentRepository.deleteCommentsByRoom_id(room_id);
        appointmentRepository.deleteAppointmentByRoom_id(room_id);
        roomRepository.deleteById(room_id);
    }

    @Override
    public Page<Room> getRoomsByUser(String isApproval, String username, Pageable pageable) {
        Optional<User> user = userRepository.findUserByUsername(username);
        return roomRepository.getUsersByUserId(isApproval, user.get().getId(), pageable);
    }

    @Override
    public Page<Room> getAllRoomsByUser(String username, Pageable pageable) {
        Optional<User> user = userRepository.findUserByUsername(username);
        return roomRepository.getAllUsersByUserId(user.get().getId(), pageable);
    }

    @Modifying
    @Transactional
    @Override
    public void updateRoom(RoomDto roomDto, Authentication auth, List<MultipartFile> imagesAdd, List<Long> imageIdsDel) {
        Room room = RoomDto.toRoom(roomDto);
        room.setIsApproval("false");
        Room oldroom = roomRepository.findById(roomDto.getRoom_id()).orElse(null);
        room.setId(roomDto.getRoom_id());
        room.setCreatedAt(oldroom.getCreatedAt());
        if (auth != null) {
            String username = auth.getName();
            Optional<User> user = userRepository.findUserByUsername(username);
            room.setUser_id(user.get().getId());
        }

        commentRepository.deleteCommentsByRoom_id(roomDto.getRoom_id());
        appointmentRepository.deleteAppointmentByRoom_id(roomDto.getRoom_id());

        if (imageIdsDel != null) {
            for (Long id : imageIdsDel) {
                Optional<Image> image = imageRepository.findById(id);
                if (room.getImage().equals(image.get().getUrl())) {
                    room.setImage("");
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
                image.setRoom_id(room.getId());
                String imageUrl = fileService.uploadFile(file); // Tải file lên Cloudinary
                image.setUrl(imageUrl);
                imageRepository.save(image);
            }
        }

        if (room.getImage().equals(null) || room.getImage().equals("")) {
            List<String> images = imageRepository.findAllImagesByRoom_id(room.getId());
            room.setImage(images.get(0));//luu anh vao roomentity
        }
        roomRepository.save(room);
    }


    @Override
    public Page<Room> getAllRoomByManyContraints(RoomFilterDataRequest request, Pageable pageable) {
        Page<Room> roomPage;
        if (request.isNull()) {
            roomPage = roomRepository.findAllByIsApproval(isApproval, pageable);
        } else {
            RoomType roomType;
            try {
                roomType = RoomTypeConverter.convertToEntityAttributeGlobal(request.getRoomType());
            } catch (Exception ex) {
                roomType = null;
            }
            roomPage = roomRepository.findAllByFilterConstraints(request.getPrice(), request.getAddress(), request.getArea(), roomType, pageable);
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
    public void addRoom(RoomDto roomDto, List<MultipartFile> images, Authentication auth) {
        Room room = RoomDto.toRoom(roomDto);
        room.setIsApproval("false");
        if (auth != null) {
            String username = auth.getName();
            Optional<User> user = userRepository.findUserByUsername(username);
            room.setUser_id(user.get().getId());
        }


        room.setImage(fileService.uploadFile((MultipartFile) images.get(0)));//luu anh vao roomentity
        roomRepository.save(room);
        Image roomImage = new Image();
        roomImage.setRoom_id(room.getId());
        roomImage.setUrl(room.getImage());
        imageRepository.save(roomImage);
        for (int i = 1; i < images.size(); i++) {//luu anh vao bang image
            Image image = new Image();
            image.setRoom_id(room.getId());
            String imageUrl = fileService.uploadFile(images.get(i));
            image.setUrl(imageUrl);
            imageRepository.save(image);
        }
        roomRepository.save(room);
    }

    @Override
    public Page<Room> getAllRoomsForAdmin(Pageable pageable) {
//        return roomRepository.findAll(pageable);
        return roomRepository.getAllOrderByCreatedAtForAdmin(pageable);
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
        List<Room> rooms = roomRepository.findAll();
        return RoomDto.toDto(rooms);
    }

}
