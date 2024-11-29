package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.OrdersSelectDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DongOrdersDAO {
    /**
     * 전체 주문 내역 조회
     * @param conn
     * @return
     */
    public List<OrdersSelectDTO> selectOrderDetailsList(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL " +
                "FROM ORDERS " +
                "JOIN USERS USING (USERS_NO)";

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapToOrdersSelectDTO(rs));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;
    }

    // 년도별 주문 내역 목록 조회
    public List<OrdersSelectDTO> selectOrderListByYear(Connection conn, String year) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL FROM ORDERS WHERE YEAR(ORDERS_DATE) = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, year);
            rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(mapToOrdersSelectDTO(rs));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;
    }

    // 월별 주문 내역 목록 조회
    public List<OrdersSelectDTO> selectOrderListByMonth(Connection conn, String year, String month) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL FROM ORDERS WHERE YEAR(ORDERS_DATE) = ? " +
                                            "AND MONTH(ORDERS_DATE) = ?";

        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, year);
            ps.setString(2, month);

            rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(mapToOrdersSelectDTO(rs));
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return orders;
    }

    // 일자별 주문 내역 목록 조회
    public List<OrdersSelectDTO> selectOrderListByDay(Connection conn, String year, String month, String day) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL " +
                    "FROM ORDERS " +
                    "WHERE YEAR(ORDERS_DATE) = ? " +
                                                "AND MONTH(ORDERS_DATE) = ?" +
                                                "AND DAY(ORDERS_DATE) = ?";

        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, year);
            ps.setString(2, month);
            ps.setString(3, day);

            rs = ps.executeQuery();

            while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;
    }
    
    // 쿼리문 결과를 OrdersSelectDTO 객체로 매핑해주는 메서드
    private OrdersSelectDTO mapToOrdersSelectDTO(ResultSet rs) throws SQLException {
        return OrdersSelectDTO.builder()
                .orderNo(rs.getInt("orders_no"))
                .orderDate(rs.getDate("orders_date"))
                .userName(rs.getString("users_name"))
                .totalPrice(rs.getInt("orders_total"))
                .build();
    }

}
