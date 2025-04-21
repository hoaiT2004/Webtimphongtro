package com.example.btl_ltw.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class PaymentTransaction {

    @Id
    private String orderId;
    private Long amount;
    private String status;
    private String username;
    @CreationTimestamp
    private Date createdAt;
    private boolean deleted = false;
}
