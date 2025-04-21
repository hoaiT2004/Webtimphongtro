package com.example.btl_ltw.service;

import com.example.btl_ltw.entity.PaymentTransaction;
import com.example.btl_ltw.entity.PaymentTransactionDetail;
import com.example.btl_ltw.model.dto.PaymentDto;
import com.example.btl_ltw.model.dto.RoomDto;
import com.example.btl_ltw.repository.PaymentTransactionDetailRepository;
import com.example.btl_ltw.repository.PaymentTransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentTransactionDetailRepository transactionDetailRepository;

    @Autowired
    private RedisService redisService;

    @Transactional
    public String createPayment(HttpSession session, String type, int value, String vnp_TmnCode, String vnp_ReturnUrl, String vnp_HashSecret, HttpServletRequest request) {

        long amount = 0;
        switch (type) {
            case "ngay":
                if (value < 3) break;
                amount = 50000L * value;
                break;
            case "tuan":
                amount = 315000L * value;
                break;
            case "thang":
                amount = 1500000L * value;
                break;
            default:
                break;
        }

        amount *= 100; // VNPAY yêu cầu nhân 100

        String orderId = UUID.randomUUID().toString();

        redisService.saveRoom(orderId, (RoomDto) session.getAttribute("room"), 10); // lưu 10 phút
        redisService.saveImages(orderId, (List<String>) session.getAttribute("images"), 10);
        redisService.saveUser(orderId, (String) session.getAttribute("username"), 10);

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", type + "x" + value);
        vnpParams.put("vnp_OrderType", "order-type");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_BankCode", "VNBANK"); // hoặc "NCB", "VNPAYQR"
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParams.put("vnp_IpAddr", getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String hash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(hash);
        return query.toString();
    }

    @Transactional
    public void save(String txnRef, long amount, String orderInfo, String username) {
        // Lưu giao dịch
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(txnRef);
        transaction.setAmount(amount);
        transaction.setStatus("SUCCESS");
        String[] info = orderInfo.split("x");
        transaction.setUsername(username);
        transaction = paymentTransactionRepository.save(transaction);

        //Chi tiết hóa đơn
        PaymentTransactionDetail detail = new PaymentTransactionDetail();
        detail.setType(info[0]);
        detail.setQuantity(Integer.parseInt(info[1]));
        detail.setPaymentID(transaction.getOrderId());
        detail.setContent("Thanh toán hóa đơn nâng cấp phòng:" + orderInfo);
        transactionDetailRepository.save(detail);
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getLocalAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public String hmacSHA512(String key, String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public Page<PaymentDto> getAllRevenueForAdmin(Pageable pageable) {
        return paymentTransactionRepository.findAllTransactions(pageable);
    }

    public List<PaymentTransaction> getAllRevenues() {
        return paymentTransactionRepository.findAll();
    }

    public Page<PaymentDto> getAllRevenueForLandlord(String name, Pageable pageable) {
        return paymentTransactionRepository.getAllRevenueForLandlord(name, pageable);
    }
}
