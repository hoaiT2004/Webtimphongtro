package com.example.btl_ltw.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {
    private final String CHATBOT_API_URL = "http://localhost:5000/chatbot";
    private final RestTemplate restTemplate = new RestTemplate();

    public String getChatbotResponse(String userMessage) {
        Map<String, String> request = new HashMap<>();
        request.put("message", userMessage);

        ResponseEntity<Map> response = restTemplate.postForEntity(CHATBOT_API_URL, request, Map.class);
        return response.getBody().get("message").toString();
    }
}
