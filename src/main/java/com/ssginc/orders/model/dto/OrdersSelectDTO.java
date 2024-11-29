package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class OrdersSelectDTO {
    // 전체 주문 내역 조회 전용 DTO
    private int orderNo; // 주문 번호
    private String orderDate; // 주문일자
    private String userName; // 주문자명
    private int totalPrice; // 총 결제 금액
}
