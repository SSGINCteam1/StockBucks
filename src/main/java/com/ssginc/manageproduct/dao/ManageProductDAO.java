package com.ssginc.manageproduct.dao;

import com.ssginc.manageproduct.vo.ManageProductVO;
import com.ssginc.util.HikariCPDataSource;

import java.sql.*;

public class ManageProductDAO {
    private HikariCPDataSource hikariCPDataSource;

    public ManageProductDAO() throws Exception {

        hikariCPDataSource = HikariCPDataSource.getInstance();

    }


    public void selectAll() throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();

        String sql = "SELECT * FROM STOCK";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            System.out.println("3. sql준비  => sql객체생성!");

            while (rs.next()) {

                int st_no = rs.getInt("st_no");
                String st_name = rs.getString("st_name");
                int st_price = rs.getInt("st_price");
                int st_quantity = rs.getInt("st_quantity");
                int st_owner = rs.getInt("st_owner");
                int st_category = rs.getInt("st_category");
                String st_unit = rs.getString("st_unit");
                int st_state = rs.getInt("st_state");

                System.out.println("NO: " + st_no + ", Name: " + st_name + ", Price: " + st_price +
                        ", Quantity: " + st_quantity + ", Owner: " + st_owner +
                        ", Category: " + st_category + ", Unit: " + st_unit +
                        ", State: " + st_state);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        con.close();


    }

    public void selectCategory(int st_category) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();

        String sql = "select * from stock where st_category = ?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, st_category); //0, 1


        ResultSet rs = ps.executeQuery();


        while (rs.next()) {
            // 결과에서 필요한 컬럼을 가져옵니다.
            int st_no = rs.getInt("st_no"); //품목번호
            String st_name = rs.getString("st_name"); //품목명
            int st_category2 = rs.getInt("st_category");
            int st_price = rs.getInt("st_price"); //가격
            String st_unit = rs.getString("st_unit"); //단위
            int st_state = rs.getInt("st_state"); //발주가능여부

  //품목번호                 품목명       	카테고리       	    가격            단위		 발주가능여부
        }
        ps.close();
        con.close();

    }










    public void selectKeyword(String st_name) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();

        String sql = "SELECT * FROM stock WHERE st_name LIKE ?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        ps = con.prepareStatement(sql);
        ps.setString(1, '%' + st_name + '%');
        rs = ps.executeQuery();

        while (rs.next()) {
            int st_no = rs.getInt("st_no");
            String st_name2 = rs.getString("st_name");
            int st_price = rs.getInt("st_price");
            int st_quantity = rs.getInt("st_quantity");
            int st_owner = rs.getInt("st_owner");
            int st_category = rs.getInt("st_category");
            String st_unit = rs.getString("st_unit");
            int st_state = rs.getInt("st_state");
            System.out.println("NO: " + st_no + ", Name: " + st_name2 + ", Price: " + st_price +
                    ", Quantity: " + st_quantity + ", Owner: " + st_owner +
                    ", Category: " + st_category + ", Unit: " + st_unit +
                    ", State: " + st_state);
        }
    }


    public void update(String st_name, int st_no) {
        String sql = "update stock set st_name = ? where st_no = ?";

    }


    public void delete(int st_no) {
        String sql = "delete from stock where st_no = ?";
    }


    public void insert(ManageProductVO bag) {
        String sql = "insert into stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state) valuse()";


    }

}
