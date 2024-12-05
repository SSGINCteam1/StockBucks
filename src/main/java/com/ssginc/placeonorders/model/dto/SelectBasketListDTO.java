package com.ssginc.placeonorders.model.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectBasketListDTO {

    private int stNo;                   // 제품번호

    private String stName;              // 제품명

    private int stPrice;                // 제품가격(단가)

    private int placeOrdersQuantity;    // 발주수량

    private int placeOrdersPrice;       // 물품 발주 가격

    private int stCategory;             // 제품 카테고리

    private String stUnit;              // 제품 단위
}
