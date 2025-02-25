package com.example.btl_ltw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class FavoriteRoom extends BaseEntity {

    private long user_id;

    private long room_id;
}
