package com.example.btl_ltw.entity;


import com.example.btl_ltw.common.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long role_id;

    @Enumerated(EnumType.STRING)
    private RoleEnum role_name;
}
