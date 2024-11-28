package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionsDTO {
    private int optNo; // 옵션번호
    private String optName; // 옵션명
    private int price; // 옵션 가격
    private int quantity; // 옵션 수량
}
