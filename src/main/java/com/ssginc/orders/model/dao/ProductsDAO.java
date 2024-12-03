package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.PrdOptDetailDTO;
import com.ssginc.orders.model.dto.WishProductsDTO;
import com.ssginc.util.HikariCPDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

public class ProductsDAO {
    DataSource dataSource;

    public ProductsDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    //품목 선택
    public ArrayList<WishProductsDTO> selectProductsListAll() {
        ArrayList<WishProductsDTO> selectProducts = new ArrayList<>();

        String sql = "SELECT p_no, p_name, p_price FROM products";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                WishProductsDTO wishProductsDTO = new WishProductsDTO();
                wishProductsDTO.setPno(rs.getInt("p_no"));
                wishProductsDTO.setPname(rs.getString("p_name"));
                wishProductsDTO.setPrice(rs.getInt("p_price"));

                selectProducts.add(wishProductsDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return selectProducts;
    }

    public ArrayList<WishProductsDTO> selectProductsListByPrdcgNo(int prdcgNo, boolean isView) {
        ArrayList<WishProductsDTO> selectProducts = new ArrayList<>();

        String sql = null;

        if (isView) {
            sql ="SELECT p_no, p_name, p_price FROM products where prdcg_no = ?";
        } else {
            sql = "SELECT p_no, p_name, p_price FROM products where prdcg_no = ? and p_state = 1";
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, prdcgNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishProductsDTO wishProductsDTO = new WishProductsDTO();
                    wishProductsDTO.setPno(rs.getInt("p_no"));
                    wishProductsDTO.setPname(rs.getString("p_name"));
                    wishProductsDTO.setPrice(rs.getInt("p_price"));

                    selectProducts.add(wishProductsDTO);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return selectProducts;
    }

    public int insertOrders(int optNo ) {
        int res = 0;

        String sql = """
                    
                """;

        try(Connection conn = dataSource.getConnection()){

        } catch (Exception e){
            e.printStackTrace();
        }


        return res;
    }
}
