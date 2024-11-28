package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DongOrdersDAO {
    /**
     * 전체 주문 내역 조회
     * @param conn
     * @return
     */
    public ArrayList<OrdersSelectDTO> selectOrderDetailsList(Connection conn) {
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
                orders.add(OrdersSelectDTO.builder()
                                .orderNo(rs.getInt("orders_no"))
                                .orderDate(rs.getDate("orders_date"))
                                .userName(rs.getString("users_name"))
                                .totalPrice(rs.getInt("orders_total"))
                                .build()
                            );
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return orders;
    }
}
