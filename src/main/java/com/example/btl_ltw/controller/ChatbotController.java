package com.example.btl_ltw.controller;

import com.example.btl_ltw.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/query")
    public String chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        return chatbotService.getChatbotResponse(userMessage);
    }
}
