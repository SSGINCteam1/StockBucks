package com.ssginc.orders.service;

import com.ssginc.orders.model.vo.OrdersVO;

import java.util.ArrayList;

public interface OrdersService {
    //1. 주문내역 전체 조회
    ArrayList<OrdersVO> selectOrdersList();

    //2. 유저벌
    ArrayList<OrdersVO> selectOrdersUsers(int usersNo);

    //3. 기간별
    ArrayList<OrdersVO> selectOrdersDate(int ordersDate);

    //4. 사용자 정의
    ArrayList<OrdersVO> selectOrdersCustomDate(int startDate, int endDate);
}
