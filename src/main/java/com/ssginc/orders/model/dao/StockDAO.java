package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.StockDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class StockDAO {
    DataSource dataSource;

    public StockDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

//    // 푸드 카테고리 조회
//    public ArrayList<StockDTO> selectFood() {
//        ArrayList<StockDTO> selectFood = new ArrayList<>();
//
//        String sql = "SELECT st_no, st_name, st_category FROM stock WHERE st_category = 0";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                StockDTO stock = new StockDTO();
//                stock.setStName(rs.getString("st_name"));
//                selectFood.add(stock);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return selectFood;
//    }
//
//    public ArrayList<StockDTO> selectMd() {
//        ArrayList<StockDTO> selectMd = new ArrayList<>();
//
//        String sql = "SELECT st_no, st_name, st_category FROM stock WHERE st_category = 1 OR st_category = 5 ORDER BY st_no";
//        try (Connection con = dataSource.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                StockDTO stock = new StockDTO();
//                stock.setStName(rs.getString("st_name"));
//                selectMd.add(stock);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return selectMd;
//    }
}
