//package com.example.btl_ltw;
//
//import com.example.btl_ltw.model.dto.RoomDto;
//import com.example.btl_ltw.service.PaymentService;
//import com.example.btl_ltw.service.RedisService;
//import com.example.btl_ltw.service.RoomService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class PaymentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void createPayment_shouldReturnRedirectUrl() throws Exception {
//        String mockQuery = "vnp_Amount=100000&vnp_TxnRef=12345";
//        String expectedUrl = "http://localhost:8080/vnpay?" + mockQuery;
//
//        when(paymentService.createPayment(Mockito.any(), Mockito.eq("ngay"), Mockito.eq(100),
//                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
//                .thenReturn(mockQuery);
//
//        mockMvc.perform(get("/api/payment/create-vnpay")
//                        .param("type", "ngay")
//                        .param("value", "100")
//                        .session(new MockHttpSession()))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedUrl));
//    }
//
//    @Test
//    void handleReturn_shouldReturnSuccess_whenResponseCodeIs00() throws Exception {
//        Map<String, String> params = new HashMap<>();
//        params.put("vnp_ResponseCode", "00");
//        params.put("vnp_TxnRef", "123abc");
//        params.put("vnp_Amount", "1000000"); // 10.000 * 100
//        params.put("vnp_OrderInfo", "ngayx10");
//
//        RoomDto roomDto = new RoomDto();
//        List<String> images = List.of("img1.jpg", "img2.jpg");
//        String username = "testuser";
//
//        when(redisService.getRoom("123abc")).thenReturn(roomDto);
//        when(redisService.getImages("123abc")).thenReturn(images);
//        when(redisService.getUser("123abc")).thenReturn(username);
//
//        mockMvc.perform(get("/api/payment/payment-return")
//                        .param("vnp_ResponseCode", "00")
//                        .param("vnp_TxnRef", "123abc")
//                        .param("vnp_Amount", "1000000")
//                        .param("vnp_OrderInfo", "ngayx10"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("payment_success"));
//    }
//
//    @Test
//    void handleReturn_shouldReturnFail_whenResponseCodeNot00() throws Exception {
//        mockMvc.perform(get("/api/payment/payment-return")
//                        .param("vnp_ResponseCode", "01")
//                        .param("vnp_TxnRef", "123abc")
//                        .param("vnp_Amount", "1000000"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("payment-fail"));
//    }
//}
