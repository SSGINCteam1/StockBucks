package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class StockDTO {
    private int stNo;
    private String stName;
    private int price;
}
