package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductionDTO {
    // 제조 상품 관련 DTO
    private int pno; // 제조 상품 번호
    private String pname; // 제조 상품 며
    private int price; // 가격
    private int quantity; // 수량
    private List<OptionsDTO> options; // 상품 옵션 리스트
}
