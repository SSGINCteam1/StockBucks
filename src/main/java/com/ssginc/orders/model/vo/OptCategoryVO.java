package com.ssginc.orders.model.vo;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptCategoryVO { //옵션 카테고리 테이블
    private int categoryNo; //카테고리 번호
    private int stNo; //재고물품 고유번호
    private String categoryName; //카테고리명
}
