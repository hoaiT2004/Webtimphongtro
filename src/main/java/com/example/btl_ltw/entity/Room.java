package com.example.btl_ltw.entity;

import com.example.btl_ltw.common.RoomType;
import com.example.btl_ltw.common.RoomTypeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Room extends BaseEntity {

    //@OneToOne(cascade = CascadeType.ALL)
    private long user_id;

    @Size(max = 255)
    private String address;

    private long capacity;

    @Min(1)
    private double price;

    @Size(max = 5000)
    private String description;

    //@Enumerated(EnumType.STRING)
    @Convert(converter = RoomTypeConverter.class)
    private RoomType roomType;

    private double area;

    private String isApproval;

    private String image;
}
