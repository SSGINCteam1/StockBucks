package com.ssginc.placeonorders.model.dto;

import lombok.Data;

@Data
public class PlaceonOrdersCheckDTO {
    private int poNo;
    private int stNo;
    private String stName;
    private int stPrice;
    private int postQuantity;
    private int subTotal;
    private int stCategory;
    private String usersName;
    private String poDate;

}
