package com.example.btl_ltw.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PaymentTransactionDetail {

    @Id
    private String paymentID;
    private String content;
    private String type; // ngày / tuần / tháng
    private int quantity;
}
