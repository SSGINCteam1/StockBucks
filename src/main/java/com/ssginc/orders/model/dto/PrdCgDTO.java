package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PrdCgDTO {
    private int prdCgNo;
    private String prdCgName; //음료 카테고리 명
}
