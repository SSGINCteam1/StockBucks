package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProductsDTO {
    // 제조 상품 관련 DTO
    private int isBeverage; // 1: 음료 / 2: 그외 상품
    private int pno; // 제조 상품 번호
    private String pname; // 제조 상품명
    private int price; // 가격
    private int prdcgNo; //카테고리 번호
    private List<OptionsDTO> options; // 상품 옵션 리스트
    private int quantity; // 수량
    private boolean isActive; // 활성화
}