package com.ssginc.placeonorders.model.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PlaceOrdersVO {
    private int poNo;
    private Timestamp poDate;
    private int poTotal;
    private int usersNo;
}
