package com.ssginc.orders.service;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dto.*;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface TimOrdersService {

    // =================================== 3. 주문 취소 ===================================
    long getDiffMinOrderDateAndNow(String date);

    int cancelOrderDetails(OrderDetailsDTO orderDetail);




    // =================================== 4. 주문 내역 조회 ===================================
    // ---------------------- 4.1. 전체 주문 내역 조회 ----------------------
    ArrayList<OrdersSelectDTO> selectOrderList(int page, int pageSize);

    int selectOrdersListRownumAll();


    // ---------------------- 4.2. 기간별 주문 내역 조회 ----------------------
    ArrayList<OrdersSelectDTO> selectOrderListByPeriod(String startDate, String endDate, int page, int pageSize);

    int selectOrdersListRownumByPeriod(String startDate, String endDate);

    // ---------------------- 4.3. 유저별 주문 내역 조회 ----------------------
    ArrayList<UsersDTO> selectUsersListByUsersName(String username, int page, int pageSize);

    ArrayList<OrdersSelectDTO> selectOrdersListByUsersNo(int usersNo, int page, int pageSize);

    int selectUsersListRownumByUsersName(String userName);

    int selectOrdersListRownumByUsers(int usersNo);


    // ---------------------- 4.4. 사용자 정의 주문 내역 조회 ----------------------

    int selectOrdersListRownumByCustomAndUsersNo(int usersNo, String startDate, String endDate);

    List<OrdersSelectDTO> selectOrderListByCustomAndUsersNo(int usersNo, String startDate, String endDate, int page, int pagsSize);


    // ---------------------- 4.5. 주문 내역 조회 유틸 메서드 ----------------------
    OrderDetailsDTO selectOrdersDetail(int orderNo);


    // =================================== 5. 품목 판매 일시 중지 ===================================
    int updateProductsIsActive(int pno, boolean isActive);


    // =================================== 1. 품목 조회 ===================================
    ArrayList<ProductsDTO> selectEtcListAll(int purpose, int type, int currentPage, int pageSize);

    ArrayList<ProductsDTO> selectProductsListByPrdcgNo(int prdcgNo, int purpose, int currentPage, int pageSize);

    ArrayList<PrdOptDTO> selectPrdOpt(int pNo);

    ArrayList<PrdCgDTO> selectPrdCgListAll(int currentPage, int pageSize);

    // =================================== 2. 품목 주문 ===================================

    int insertOrders(List<ProductsDTO> products, int usersNo);

    int selectProductsListRownumByPrdcgNo(int prdcgNo, int purpose);

    int selectEtcListRownumAll(int type, boolean isOrder);

    int selectPrdCgListRownumAll();

}
