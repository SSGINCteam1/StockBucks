package com.ssginc.placeonorders.model.dao;

import com.ssginc.placeonorders.model.dto.HoonSelectStockListDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HoonPlaceOnOrdersDAO {
    private final DataSource dataSource;

    public HoonPlaceOnOrdersDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 지점 재고 전체 조회
    public List<HoonSelectStockListDTO> selectAllStockList() {
        List<HoonSelectStockListDTO> stockList = null;
        String sql = "SELECT st_no, st_name, st_quantity, st_category, st_unit FROM stock WHERE st_owner = 1";

        // DB와 connection 연결 및 SQL문 전송
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (stockList == null) {
                        stockList = new ArrayList<>();
                    }

                    // 값 전달을 위한 DTO 생성
                    HoonSelectStockListDTO dto = new HoonSelectStockListDTO();

                    // SQL문을 통해 불러온 field의 값들로 DTO setting
                    dto.setStNo(rs.getInt("st_no"));
                    dto.setStName(rs.getString("st_name"));
                    dto.setStQuantity(rs.getInt("st_quantity"));
                    dto.setStCategory(rs.getInt("st_category"));
                    dto.setStUnit(rs.getString("st_unit"));

                    // DTO List에 DTO 추가
                    stockList.add(dto);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return stockList;
    }

    // 지점 재고 카테고리별 조회
    public List<HoonSelectStockListDTO> selectStockListByCategory(int category) {
        List<HoonSelectStockListDTO> stockList = null;
        String sql = "SELECT st_no, st_name, st_quantity, st_category, st_unit FROM stock WHERE st_owner = 1 and st_category = ?";

        // DB와 connection 연결 및 SQL문 전송
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, category);     // SQL문 st_category의 값 setting

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (stockList == null) {
                        stockList = new ArrayList<>();
                    }

                    // 값 전달을 위한 DTO 생성
                    HoonSelectStockListDTO dto = new HoonSelectStockListDTO();

                    // SQL문을 통해 불러온 field의 값들로 DTO setting
                    dto.setStNo(rs.getInt("st_no"));
                    dto.setStName(rs.getString("st_name"));
                    dto.setStQuantity(rs.getInt("st_quantity"));
                    dto.setStCategory(rs.getInt("st_category"));
                    dto.setStUnit(rs.getString("st_unit"));

                    // DTO List에 DTO 추가
                    stockList.add(dto);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return stockList;
    }

    // 지점 재고 키워드 검색
    public List<HoonSelectStockListDTO> selectStockListByKeyword(String searchKeyword) {
        List<HoonSelectStockListDTO> stockList = null;
        String sql = "SELECT st_no, st_name, st_quantity, st_category, st_unit FROM stock WHERE st_owner and st_name LIKE ?";

        // DB와 connection 연결 및 SQL문 전송
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // SQL문의 LIKE절에 대한 파라미터를 전달받은 keyword로 setting
            ps.setString(1, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (stockList == null) {
                        stockList = new ArrayList<>();
                    }

                    // 값 전달을 위한 DTO 생성
                    HoonSelectStockListDTO dto = new HoonSelectStockListDTO();

                    // SQL문을 통해 불러온 field의 값들로 DTO setting
                    dto.setStNo(rs.getInt("st_no"));
                    dto.setStName(rs.getString("st_name"));
                    dto.setStQuantity(rs.getInt("st_quantity"));
                    dto.setStCategory(rs.getInt("st_category"));
                    dto.setStUnit(rs.getString("st_unit"));

                    // DTO List에 DTO 추가
                    stockList.add(dto);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return stockList;
    }

}
