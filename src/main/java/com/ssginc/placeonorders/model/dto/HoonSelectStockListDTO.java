package com.ssginc.placeonorders.model.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoonSelectStockListDTO {

    private int stNo;           // 제품번호

    private String stName;      // 제품명

    private int stQuantity;     // 재고수량

    private int stCategory;     // 제품 카테고리

    private String stUnit;      // 제품 단위

}
