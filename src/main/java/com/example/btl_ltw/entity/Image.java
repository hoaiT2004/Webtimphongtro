package com.example.btl_ltw.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@OneToOne(fetch = FetchType.LAZY)
    //Mối quan hệ 1:1 với room
    private long room_id;

    private String url;
}
