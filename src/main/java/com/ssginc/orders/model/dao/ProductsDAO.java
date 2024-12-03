package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.WishProductsDTO;
import com.ssginc.util.HikariCPDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

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

    public ArrayList<WishProductsDTO> selectProductsListByPrdcgNo(int prdcgNo) {
        ArrayList<WishProductsDTO> selectProducts = new ArrayList<>();

        String sql = "SELECT p_no, p_name, p_price FROM products where prdcg_no = ?";

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
}
