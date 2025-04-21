package com.example.btl_ltw.model.dto;

import com.example.btl_ltw.common.RoomType;
import com.example.btl_ltw.entity.Room;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoomDto {

    private long room_id;

    private long user_id;

    private String address;

    private long capacity;

    private double price;

    private String description;

    private String roomType;

    private double area;

    private String isApproval;

    private String image;

    private Date createdAt;

    @Min(1)
    private double waterPrice;

    @Min(1)
    private double electricityPrice;

    private long viewNumber;

    @Builder.Default
    private boolean vipStatus = false;

    @Builder.Default
    private int vipDateNumber = 0;

    public static RoomDto toDto(Room room) {
        if (room == null) {
            return null;
        }
        var roomDto = RoomDto.builder()
                .room_id(room.getId())
                .user_id(room.getUser_id())
                .address(room.getAddress())
                .capacity(room.getCapacity())
                .price(room.getPrice())
                .description(room.getDescription())
                .roomType(room.getRoomType().name())
                .area(room.getArea())
                .isApproval(room.getIsApproval())
                .image(room.getImage())
                .viewNumber(room.getViewNumber())
                .waterPrice(room.getWaterPrice())
                .electricityPrice(room.getElectricityPrice())
                .createdAt(room.getCreatedAt())
                .vipDateNumber(room.getVipDateNumber())
                .vipStatus(room.isVipStatus())
                .build();
        if (room.getRoomType().name().equals("CHUNG_CHU")) {
            roomDto.setRoomType("Chung chủ");
        } else {
            roomDto.setRoomType("Không chung chủ");
        }
        return roomDto;
    }

    public static Room toRoom(RoomDto room) {
        if (room == null) {
            return null;
        }
        Room res =  Room.builder()
                .user_id(room.user_id)
                .address(room.address)
                .capacity(room.capacity)
                .price(room.price)
                .waterPrice(room.getWaterPrice())
                .electricityPrice(room.getElectricityPrice())
                .description(room.description)
                .roomType(RoomType.valueOf((room.roomType.equals("Không chung chủ") || room.roomType.equals("KHONG_CHUNG_CHU"))?"KHONG_CHUNG_CHU":"CHUNG_CHU"))
                .area(room.area)
                .viewNumber(room.getViewNumber())
                .isApproval(room.isApproval)
                .image(room.image)
                .vipDateNumber(room.getVipDateNumber())
                .vipStatus(room.isVipStatus())
                .build();
        res.setCreatedAt(room.getCreatedAt());
        return res;
    }

    public static List<RoomDto> toDto(List<Room> rooms) {
        return rooms.stream()
                .map(RoomDto::toDto)
                .collect(Collectors.toList());
    }

    public static List<Room> toRoom(List<RoomDto> roomDtos) {
        return roomDtos.stream()
                .map(RoomDto::toRoom)
                .collect(Collectors.toList());
    }
}
