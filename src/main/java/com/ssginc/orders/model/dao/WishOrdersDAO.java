package com.ssginc.orders.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class WishOrdersDAO {
    public int insertOrders(Connection conn, int totalPrdQuantity, int totalPrice, int usersNo) {

        int generatedKey = 0; // 생성된 orders의 기본키

        String sql = """
                INSERT INTO orders
                (orders_quantity, orders_total, users_no)
                VALUES
                (?, ?, ?)
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 기본키 가져오기 위한 목적
            ps.setInt(1, totalPrdQuantity);
            ps.setInt(2, totalPrice);
            ps.setInt(3, usersNo);

            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys() ) { // 기본키 값 획득
                if(rs.next()) {
                    generatedKey = rs.getInt(1);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return generatedKey;
    }

    public int insertOrdersPrd(Connection conn, int ordersKey, int quantity, int pno) {
        int generatedKey = 0; // 생성된 orders의 기본키

        String sql = """
                INSERT INTO orders_prd
                (opd_quantity, orders_no, p_no)
                VALUES
                (?, ?, ?)
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // 기본키 가져오기 위한 목적
            ps.setInt(1, quantity);
            ps.setInt(2, ordersKey);
            ps.setInt(3, pno);

            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys() ) { // 기본키 값 획득
                if (rs.next()) {
                    generatedKey = rs.getInt(1);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return generatedKey;
    }

    public int insertOrdersOpt(Connection conn, int ordPrdKey, int optNo, int price, int quantity, String optName) {

        int res = 0;

        String sql = null;

        if (quantity == 0){
            sql = """
                INSERT INTO orders_opt
                (ord_prd_no, opt_no, oropt_price, oropt_name)
                VALUES
                (?, ?, ?, ?)
                """;
        } else {
            sql = """
                INSERT INTO orders_opt
                (ord_prd_no, opt_no, oropt_price, oropt_name, oropt_quantity)
                VALUES
                (?, ?, ?, ?, ?)
                """;
        }

        try(PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ordPrdKey);
            ps.setInt(2, optNo);
            ps.setInt(3, price);
            ps.setString(4, optName);

            if (quantity != 0){
                ps.setInt(5, quantity);
            }


            res = ps.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    public int insertOrdersStock(Connection conn, int ordersKey, int pno, int quantity) {

        int res = 0;

        String sql = """
                
                INSERT INTO orders_stock
                (orders_no, st_no, ost_quantity)
                VALUES
                (?, ?, ?)
                
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, ordersKey);
            ps.setInt(2, pno);
            ps.setInt(3, quantity);

            res = ps.executeUpdate();

        } catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    public int updateStockForOrders(Connection conn, int stockNo, int stockConsumption) {

        int res = 0;

        String sql = """
                UPDATE stock
                SET st_quantity = st_quantity - ?
                WHERE st_no = ?
                    """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, stockConsumption);
            ps.setInt(2, stockNo);

            res = ps.executeUpdate();

        } catch ( Exception e ){
            e.printStackTrace();
        }

        return res;

    }
}
