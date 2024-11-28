package com.ssginc.orders.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdPrdVO {     //주문-제조상품 중개테이블
    private int ordersNo; //주문번호 (외래키)
    private int pNo; //제조상품 (외래키)
    private int opdQuantity; //주문수량
}
