package com.example.btl_ltw.repository;

import com.example.btl_ltw.entity.PaymentTransaction;
import com.example.btl_ltw.entity.PaymentTransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentTransactionDetailRepository extends JpaRepository<PaymentTransactionDetail, String> {
}
