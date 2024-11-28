package com.ssginc.orders.model.dto;

import com.ssginc.orders.model.vo.ProductionVO;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
public class OrderDetailsDTO {
    // 주문 내역 조회 전용 DTO
    private int orderNo; // 주문 번호
    private Date orderDate; // 주문일자
    private String userName; // 주문자명
    private List<ProductionDTO> products; // 주문 상품 리스트
    private int totalPrice; // 총 결제 금액


}
