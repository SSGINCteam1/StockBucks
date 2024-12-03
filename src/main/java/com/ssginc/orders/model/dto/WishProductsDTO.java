package com.ssginc.orders.model.dto;

import lombok.Data;

@Data
public class WishProductsDTO {
    // 제조 상품 관련 DTO
    private int pno; // 제조 상품 번호
    private String pname; // 제조 상품 명
    private int price; // 가격
    private int prdcgNo; //카테고리 번호

}
