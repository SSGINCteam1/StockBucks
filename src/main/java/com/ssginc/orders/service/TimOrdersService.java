package com.ssginc.orders.service;

import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;

import java.util.ArrayList;
import java.util.List;

public interface TimOrdersService {
    // 주문 내역 조회

    // 1. 전체 조회
    ArrayList<OrdersSelectDTO> selectOrderList(int page, int pageSize);
    // 2. 기간별 조회
    ArrayList<OrdersSelectDTO> selectOrderListByPeriod(String period, String date);
    // 3. 유저별 조회
    ArrayList<OrdersSelectDTO> selectOrderListByUserNo(String user);
    // 4. 사용자 정의 조회
    ArrayList<OrdersSelectDTO> selectOrderListByCustom(String startDate, String endDate);

    int selectOrdersAllRownum();

    OrderDetailsDTO selectOrdersDetail(int orderNo);
}
