package com.ssginc.orders.model.vo;

import lombok.Data;

@Data
public class StockVO {
    private int stNo;
    private String stName;
    private int stPrice;
    private int stQuantity;
    private int stOwner;
    private int stCategory;
    private String stUnit;
    private int stState;
}
