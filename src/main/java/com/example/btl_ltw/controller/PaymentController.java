package com.example.btl_ltw.controller;

import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.service.PaymentService;
import com.example.btl_ltw.service.RedisService;
import com.example.btl_ltw.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RoomService roomService;

    @Value("${vnp.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnp.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnp.url}")
    private String vnp_Url;

    @Value("${vnp.returnUrl}")
    private String vnp_ReturnUrl;

    @Autowired
    private RedisService redisService;

    @GetMapping("/create-vnpay")
    public ResponseEntity<String> createPayment(
            @RequestParam String type,
            @RequestParam int value,
            HttpSession session,
            HttpServletRequest request
    ) {
        String query = paymentService.createPayment(session, type, value, vnp_TmnCode, vnp_ReturnUrl, vnp_HashSecret, request);
        String vnpUrlFull = vnp_Url + "?" + query;
        return ResponseEntity.ok(vnpUrlFull);
    }


    @GetMapping("/payment-return")
    public String handleReturn(@RequestParam Map<String, String> params) {

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        long amount = Long.parseLong(params.get("vnp_Amount")) / 100;

        RoomDto roomDto = redisService.getRoom(txnRef);
        List<String> files = redisService.getImages(txnRef);
        String username = redisService.getUser(txnRef);

        if ("00".equals(responseCode)) {
            String orderInfo = params.get("vnp_OrderInfo");
            paymentService.save(txnRef, amount, orderInfo, username);
            String[] info = orderInfo.split("x");
            switch (info[0]) {
                case "ngay":
                    roomDto.setVipDateNumber(Integer.parseInt(info[1]));
                    break;
                case "tuan":
                    roomDto.setVipDateNumber(Integer.parseInt(info[1]) * 7);
                    break;
                case "thang":
                    roomDto.setVipDateNumber(Integer.parseInt(info[1]) * 31);
                    break;
            }
            roomDto.setVipStatus(true);
            roomService.addRoom(roomDto, files, username);
            return "payment_success";
        } else {
            return "payment-fail";
        }
    }
}
