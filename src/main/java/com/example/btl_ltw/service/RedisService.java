package com.example.btl_ltw.service;

import com.example.btl_ltw.model.dto.RoomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveRoom(String txnRef, RoomDto roomDto, long timeoutInMinutes) {
        redisTemplate.opsForValue().set("room:" + txnRef, roomDto, timeoutInMinutes, TimeUnit.MINUTES);
    }

    public void saveRoomID(String txnRef, Long roomID, long timeoutInMinutes) {
        redisTemplate.opsForValue().set("roomID:" + txnRef, Long.parseLong(roomID+""), timeoutInMinutes, TimeUnit.MINUTES);
    }

    public RoomDto getRoom(String txnRef) {
        return (RoomDto) redisTemplate.opsForValue().get("room:" + txnRef);
    }

    public Integer getRoomID(String txnRef) {
        return (Integer) redisTemplate.opsForValue().get("roomID:" + txnRef);
    }

    public void saveUser(String txnRef, String username, long timeoutInMinutes) {
        redisTemplate.opsForValue().set("user:" + txnRef, username, timeoutInMinutes, TimeUnit.MINUTES);
    }

    public String getUser(String txnRef) {
        return (String) redisTemplate.opsForValue().get("user:" + txnRef);
    }

    public void saveImages(String txnRef, List<String> images, long timeoutInMinutes) {
        redisTemplate.opsForValue().set("images:" + txnRef, images, timeoutInMinutes, TimeUnit.MINUTES);
    }

    public List<String> getImages(String txnRef) {
        return (List<String>) redisTemplate.opsForValue().get("images:" + txnRef);
    }

    public void delete(String txnRef) {
        redisTemplate.delete("room:" + txnRef);
        redisTemplate.delete("images:" + txnRef);
        redisTemplate.delete("user:" + txnRef);
    }

    public void deleteForUpgradeTask(String txnRef) {
        redisTemplate.delete("roomID:" + txnRef);
        redisTemplate.delete("user:" + txnRef);
    }
}
