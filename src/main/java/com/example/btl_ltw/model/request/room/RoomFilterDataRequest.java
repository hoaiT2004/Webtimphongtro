package com.example.btl_ltw.model.request.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomFilterDataRequest {
    private String price, area,address, roomType;
    private String isVip;
    private Boolean isVipBool = null;

    public boolean isNull() {
        return (price == null || price.equals("null")) && (area == null || area.equals("null")) && (address == null || address.equals("null")) && (roomType == null || roomType.equals("null"));
    }

    public boolean emptyVip() {
        return isVip == null || isVip.equals("");
    }

    public void setVip() {
        if ("true".equalsIgnoreCase(isVip)) {
            isVipBool = true;
        } else if ("false".equalsIgnoreCase(isVip)) {
            isVipBool = false;
        }
    }
}
