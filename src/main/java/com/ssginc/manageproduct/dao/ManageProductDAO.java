package com.ssginc.manageproduct.dao;

import com.ssginc.manageproduct.vo.ManageProductVO;
import com.ssginc.util.HikariCPDataSource;

import java.sql.*;
import java.util.ArrayList;

public class ManageProductDAO implements ManageProductDAOInterface {
    private HikariCPDataSource hikariCPDataSource;

    public ManageProductDAO() throws Exception {

        hikariCPDataSource = HikariCPDataSource.getInstance();

    }


    @Override
    public ArrayList<ManageProductVO> selectAll() throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();
        ArrayList<ManageProductVO> list = new ArrayList<>();

        String sql = "SELECT * FROM STOCK";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int st_no = rs.getInt("st_no");
                String st_name = rs.getString("st_name");
                int st_price = rs.getInt("st_price");
                int st_quantity = rs.getInt("st_quantity");
                int st_owner = rs.getInt("st_owner");
                int st_category = rs.getInt("st_category");
                String st_unit = rs.getString("st_unit");
                int st_state = rs.getInt("st_state");

//               // System.out.println("NO: " + st_no + ", Name: " + st_name + ", Price: " + st_price +
//                        ", Quantity: " + st_quantity + ", Owner: " + st_owner +
//                        ", Category: " + st_category + ", Unit: " + st_unit +
//                        ", State: " + st_state);
//
                ManageProductVO vo = new ManageProductVO();
                vo.setSt_no(st_no);
                vo.setSt_name(st_name);
                vo.setSt_price(st_price);
                vo.setSt_quantity(st_quantity);
                vo.setSt_owner(st_owner);
                vo.setSt_category(st_category);
                vo.setSt_unit(st_unit);
                vo.setSt_state(st_state);
                list.add(vo);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        con.close();
        return list;

    }

    @Override
    public ArrayList<ManageProductVO> selectCategory(int st_category) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();
        //ArrayList생성
        ArrayList<ManageProductVO> list = new ArrayList<>();
        String sql = "select * from stock where st_category = ?";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, st_category); //0, 1


        ResultSet rs = ps.executeQuery();


        while (rs.next()) {
            // 결과에서 필요한 컬럼을 가져옵니다.
            int st_no = rs.getInt("st_no"); //품목번호
            String st_name = rs.getString("st_name"); //품목명
            int st_category2 = rs.getInt("st_category");//카테고리
            int st_price = rs.getInt("st_price"); //가격
            String st_unit = rs.getString("st_unit"); //단위
            int st_state = rs.getInt("st_state"); //발주가능여부
            //vo만들어 위의 값들 다 넣어주고
            //list에 vo추가
            ManageProductVO vo = new ManageProductVO();
            vo.setSt_no(st_no);
            vo.setSt_name(st_name);
            vo.setSt_price(st_price);
            vo.setSt_category(st_category);
            vo.setSt_unit(st_unit);
            vo.setSt_state(st_state);
            list.add(vo);
            //품목번호                 품목명          카테고리               가격            단위       발주가능여부
        }
        ps.close();
        con.close();
        return list;
    }

    @Override
    public ArrayList<ManageProductVO> selectKeyword(String st_name) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();

        //ArrayList생성
        ArrayList<ManageProductVO> list = new ArrayList<>();
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
//            System.out.println("NO: " + st_no + ", Name: " + st_name2 + ", Price: " + st_price +
//                    ", Quantity: " + st_quantity + ", Owner: " + st_owner +
//                    ", Category: " + st_category + ", Unit: " + st_unit +
//                    ", State: " + st_state);
            ManageProductVO vo = new ManageProductVO();
            vo.setSt_no(st_no);
            vo.setSt_name(st_name);
            vo.setSt_price(st_price);
            vo.setSt_quantity(st_quantity);
            vo.setSt_owner(st_owner);
            vo.setSt_category(st_category);
            vo.setSt_unit(st_unit);
            vo.setSt_state(st_state);
            list.add(vo);
        }




        ps.close();
        con.close();
        return list;
    }


    @Override
    public boolean update(String st_name, int st_no) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();
        String sql = "update stock set st_name = ? where st_no = ?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, st_name); //
        ps.setInt(2, st_no);

        int result = ps.executeUpdate();
        ps.close();
        con.close(); //관련 자원들 메모리에서 해제!


        return result > 0;
    }
    @Override
    public boolean delete(int st_no) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();
        String sql = "delete from stock where st_no = ?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, st_no);

        int result = ps.executeUpdate();

        ps.close();
        con.close();

        return result > 0;
    }



    @Override
    public int insert(ManageProductVO bag) throws Exception {
        Connection con = hikariCPDataSource.getDataSource().getConnection();
        String sql = "insert into stock (st_name, st_price, st_quantity, st_owner, st_category, st_unit, st_state) values (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql);


        ps.setString(1, bag.getSt_name());
        ps.setInt(2, bag.getSt_price());
        ps.setInt(3, bag.getSt_quantity());
        ps.setInt(4, bag.getSt_owner());
        ps.setInt(5, bag.getSt_category());
        ps.setString(6, bag.getSt_unit());
        ps.setInt(7, bag.getSt_state());

        int result = ps.executeUpdate();



        ps.close();
        con.close();

        return result;
    }


}