package com.example.btl_ltw.repository;

import com.example.btl_ltw.entity.PaymentTransaction;
import com.example.btl_ltw.model.dto.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    @Query("SELECT new com.example.btl_ltw.model.dto.PaymentDto(p.orderId, p.amount, pd.type, pd.quantity, pd.content, p.status) " +
            "FROM PaymentTransaction p " +
            "INNER JOIN PaymentTransactionDetail pd " +
            "ON p.orderId = pd.paymentID " +
            "WHERE p.username = :name")
    Page<PaymentDto> getAllRevenueForLandlord(String name, Pageable pageable);

    @Query("SELECT new com.example.btl_ltw.model.dto.PaymentDto(p.orderId, p.amount, pd.type, pd.quantity, pd.content, p.status, p.username, p.createdAt) FROM PaymentTransaction p " +
            "INNER JOIN PaymentTransactionDetail pd " +
            "ON p.orderId = pd.paymentID")
    Page<PaymentDto> findAllTransactions(Pageable pageable);
}
