package com.example.btl_ltw.service.Impl;

import com.example.btl_ltw.entity.RoomStar;
import com.example.btl_ltw.repository.RoomStarRepository;
import com.example.btl_ltw.service.RoomStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomStarServiceImpl implements RoomStarService {

    @Autowired
    private RoomStarRepository roomStarRepository;

    @Override
    @Transactional
    public void addStar(long roomId, long rating, String username) {
        if (roomStarRepository.findRoomStarByRoomIdAndUsername(roomId, username) != null) {
            roomStarRepository.updateRoomStarByRoomIdAndUsername(roomId, username, rating);
        } else {
            roomStarRepository.save(RoomStar.builder()
                    .roomId(roomId)
                    .rating(rating)
                    .username(username)
                    .build());
        }
    }

    @Override
    public int StarTotal(long roomId, long rating) {
        return roomStarRepository.StarTotal(roomId, rating);
    }

    @Override
    public float StarAverage(long roomId) {
        int star5Total = StarTotal(roomId, 5);
        int star4Total = StarTotal(roomId, 4);
        int star3Total = StarTotal(roomId, 3);
        int star2Total = StarTotal(roomId, 2);
        int star1Total = StarTotal(roomId, 1);
        if (star1Total == 0 && star2Total == 0 && star3Total == 0 && star4Total == 0 && star5Total == 0) {
            return 0;
        }
        return (float) (star1Total + 2 * star2Total + 3 * star3Total + 4 * star4Total + 5 * star5Total) / (star1Total + star2Total + star3Total + star4Total + star5Total);
    }
}
