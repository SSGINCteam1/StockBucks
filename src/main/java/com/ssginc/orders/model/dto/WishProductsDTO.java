package com.ssginc.orders.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class WishProductsDTO {
    // 제조 상품 관련 DTO
    private int isBeverage; // 1: 음료 / 2: 그외 상품
    private int pno; // 제조 상품 번호
    private String pname; // 제조 상품 명
    private int price; // 가격
    private int prdcgNo; //카테고리 번호
    private List<OptionsDTO> options; // 상품 옵션 리스트
    private int quantity; // 수량
}
