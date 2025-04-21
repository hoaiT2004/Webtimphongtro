package com.example.btl_ltw.service;

public interface RoomStarService {

    void addStar(long roomId, long rating, String username);

    int StarTotal(long roomId, long rating);

    float StarAverage(long roomId);
}
