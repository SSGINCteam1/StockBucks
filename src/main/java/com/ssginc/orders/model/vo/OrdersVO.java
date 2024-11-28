package com.ssginc.orders.model.vo;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class OrdersVO {     //주문테이블
    private int ordersNo; //주문번호
    private Date ordersDate; //주문일시
    private int ordersQuantity; //주문수량
    private double ordersTotal; //주문총액
    private int usersNo; //유저번호
}
