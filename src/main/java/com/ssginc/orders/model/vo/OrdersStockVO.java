package com.ssginc.orders.model.vo;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdersStockVO {   //주문-재고물품 중개테이블
    private int ordersNo; //주문번호
    private int stNo; //잡화 고유번호
    private int ostQuantity; //주문수량
}
