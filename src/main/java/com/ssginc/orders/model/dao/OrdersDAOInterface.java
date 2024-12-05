package com.ssginc.orders.model.dao;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dto.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface OrdersDAOInterface {
    int deleteOrdersForCancelOrder(Connection conn, int orderNo);

    int deleteOrdersOptForCancelOrder(Connection conn, int orderNo);

    int deleteOrdersPrdForCancelOrder(Connection conn, int orderNo);

    int deleteOrdersStockForCancelOrder(Connection conn, int orderNo);

    int updateStockForRestore(Connection conn, int stockNo, int stockConsumption);

    List<ConsumptionDTO> selectProductConsumptionList(Connection conn, int productNo);

    List<ConsumptionDTO> selectOptConsumptionList(Connection conn, int optNo);

    ArrayList<OrdersSelectDTO> selectOrderList(Connection conn, int pageSize, int offset);

    int selectOrdersListRownumAll(Connection conn);

    ArrayList<OrdersSelectDTO> selectOrderListByPeriod(Connection conn, String start, String end, int pageSize, int offset);

    int selectOrdersListRownumByPeriod(Connection conn, String start, String end);

    // ---------------------- 4.3. 유저별 주문 내역 조회 ----------------------
    ArrayList<UsersDTO> selectUsersListByUsersName(Connection conn, String username, int pageSize, int offset);

    int selectUsersListRownumByUsersName(Connection conn, String userName);

    int selectOrdersListRownumByUsers(Connection conn, int usersNo);

    ArrayList<OrdersSelectDTO> selectOrdersListByUsers(Connection conn, int usersNo, int pageSize, int offset);

    // ---------------------- 4.4. 사용자 정의 주문 내역 조회 ----------------------
    int selectOrdersListRownumByCustom(Connection conn, String startDate, String endDate);

    ArrayList<OrdersSelectDTO> selectOrdersListByCustom(Connection conn, LocalDate start, LocalDate end, int pageSize, int offset);

    int selectOrdersListRownumByCustomAndUsersNo(Connection conn, int usersNo, LocalDate start, LocalDate end);

    ArrayList<OrdersSelectDTO> selectOrderListByCustomAndUsersNo(Connection conn, int usersNo, LocalDate start, LocalDate end, int pageSize, int offset);

    // 주문 세부 객체 select
    OrderDetailsDTO selectOrdersDetails(Connection conn, int orderNo);

    // 쿼리문 결과를 OrdersSelectDTO 객체로 매핑해주는 메서드
    OrdersSelectDTO mapToOrdersSelectDTO(ResultSet rs) throws SQLException;

    // =================================== 5. 품목 판매 중지 ===================================
    int updateProductsIsActive(Connection conn, int state, int pno);

    // =================================== 1. 품목 조회 ===================================
    ArrayList<ProductsDTO> selectEtcListAll(Connection conn, int type, boolean isOrder, int pageSize, int offset);

    int selectEtcListRownumAll(Connection conn, int type, boolean isOrder);

    ArrayList<PrdCgDTO> selectPrdCgListAll(Connection conn);

    int selectPrdCgListRownumAll(Connection conn);

    ArrayList<ProductsDTO> selectProductsListByPrdcgNo(Connection conn, int prdcgNo, boolean isOrder, int pageSize, int offset);

    int selectProductsListRownumByPrdcgNo(Connection conn, int prdcgNo, boolean isOrder);

    int insertOrders(Connection conn, int totalPrdQuantity, int totalPrice, int usersNo);

    int insertOrdersPrd(Connection conn, int ordersKey, int quantity, int pno);

    int insertOrdersOpt(Connection conn, int ordPrdKey, int optNo, int price, int quantity, String optName);

    int insertOrdersStock(Connection conn, int ordersKey, int pno, int quantity);

    int updateStockForOrders(Connection conn, int stockNo, int stockConsumption);
}
