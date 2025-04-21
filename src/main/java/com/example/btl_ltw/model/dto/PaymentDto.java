package com.example.btl_ltw.model.dto;

import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentDto {
    private String orderID;
    private long amount;
    private String type;
    private int quantity;
    private String content;
    private String status;
    private String username;
    private String createdAt;

    public PaymentDto(String orderID, long amount, String type, int quantity, String content, String status) {
        this.orderID = orderID;
        this.amount = amount;
        this.type = type;
        this.quantity = quantity;
        this.content = content;
        this.status = status;
    }

    public PaymentDto(String orderID, long amount, String type, int quantity, String content, String status, String username, Date createdAt) {
        this.orderID = orderID;
        this.amount = amount;
        this.username = username;
        this.type = type;
        this.quantity = quantity;
        this.content = content;
        this.status = status;
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.createdAt = dt.format(((Timestamp)createdAt).toLocalDateTime());
    }
}
