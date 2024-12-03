package com.ssginc.placeonorders.model.dao;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.placeonorders.model.dto.PlaceOnOrdersHistoryDTO;
import com.ssginc.placeonorders.model.dto.PlaceOnOrdersInsertBagDTO;
import com.ssginc.placeonorders.model.dto.PlaceonOrdersCheckDTO;
import com.ssginc.placeonorders.model.dto.PlaceonOrdersDTO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersStockVO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersVO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlaceOnOrdersDAO {
    public DataSource dataSource;

    public PlaceOnOrdersDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 전체 데이터 조회
    public ArrayList<PlaceonOrdersDTO> selectAllOrderableStocks(Connection con) {
        ArrayList<PlaceonOrdersDTO> list = new ArrayList<>();
        String sql = "select st_no, st_name, st_price, st_quantity, st_category, st_unit " +
                "from stock where st_state = 1 and st_owner = 1;";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // DTO 객체 생성 및 값 설정
                PlaceonOrdersDTO user = new PlaceonOrdersDTO();
                user.setStNo(rs.getInt("st_no"));
                user.setStName(rs.getString("st_name"));
                user.setStPrice(rs.getInt("st_price"));
                user.setStQuantity(rs.getInt("st_quantity"));
                user.setStCategory(rs.getInt("st_category"));
                user.setStUnit(rs.getString("st_unit"));

                // 리스트에 추가
                list.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stock data", e);
        }

        return list; // 결과 반환
    }

    // 특정 카테고리 데이터 조회
    public ArrayList<PlaceonOrdersDTO> selectAllOrderableStocksByCategory(Connection con, int st_category) {
        ArrayList<PlaceonOrdersDTO> list = new ArrayList<>();
        String sql = "select st_no, st_name, st_price, st_quantity, st_category, st_unit " +
                "from stock where st_state = 1 and st_category = ? and st_owner = 1;";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, st_category); // 파라미터 설정
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // DTO 객체 생성 및 값 설정
                    PlaceonOrdersDTO user = new PlaceonOrdersDTO();
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setStQuantity(rs.getInt("st_quantity"));
                    user.setStCategory(rs.getInt("st_category"));
                    user.setStUnit(rs.getString("st_unit"));

                    // 리스트에 추가
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stock data by category", e);
        }

        return list;
    }


    // 검색 데이터 조회
    public ArrayList<PlaceonOrdersDTO> selectAllOrderableStocksByKeyword(Connection con, String keyword) {
        ArrayList<PlaceonOrdersDTO> list = new ArrayList<>();
        String sql = "SELECT st_no, st_name, st_price, st_quantity, st_category, st_unit " +
                "FROM stock " +
                "WHERE st_state = 1 AND st_name LIKE ? AND st_owner = 1";


        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%"); // 파라미터 설정
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // DTO 객체 생성 및 값 설정
                    PlaceonOrdersDTO user = new PlaceonOrdersDTO();
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setStQuantity(rs.getInt("st_quantity"));
                    user.setStCategory(rs.getInt("st_category"));
                    user.setStUnit(rs.getString("st_unit"));

                    // 리스트에 추가
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            System.out.println("오류");

        }


        return list;
    }

    public ArrayList<PlaceonOrdersCheckDTO> selectAllOrderableStockChecks(Connection con) {
        ArrayList<PlaceonOrdersCheckDTO> list = new ArrayList<>();
        String sql = "SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, " +
                "(s.st_price * post.post_quantity) AS sub_total, s.st_category, po.po_date, u.users_name " +
                "FROM place_orders_stock post " +
                "INNER JOIN stock s ON post.st_no = s.st_no " +
                "INNER JOIN place_orders po ON post.po_no = po.po_no " +
                "INNER JOIN users u ON po.users_no = u.users_no;";


        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // DTO 객체 생성 및 값 설정
                PlaceonOrdersCheckDTO user = new PlaceonOrdersCheckDTO();
                user.setPoNo(rs.getInt("po_no"));
                user.setStNo(rs.getInt("st_no"));
                user.setStName(rs.getString("st_name"));
                user.setStPrice(rs.getInt("st_price"));
                user.setPostQuantity(rs.getInt("post_quantity"));
                user.setStCategory(rs.getInt("st_category"));
                user.setUsersName(rs.getString("users_name"));
                user.setPoDate(rs.getString("po_date"));

                list.add(user);
            }
        } catch (SQLException e) {
            System.out.println("오류");
        }

        return list;

    }

    public void placeOnOrdersInsertBag(Connection con, PlaceOnOrdersInsertBagDTO dto) {
        boolean running = true;

        String sql = "insert into place_orders_basket (users_no, st_no, pob_quantity) values (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dto.getUsers_no());
            ps.setInt(2, dto.getStNo());
            ps.setInt(3, dto.getPobQuantity());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("오류");
        }
    }

    public ArrayList<PlaceOnOrdersHistoryDTO> HistoryplaceOrdersStockByCategory(Connection con, int category) {
        ArrayList<PlaceOnOrdersHistoryDTO> list = new ArrayList<>();
        String sql = "SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, " +
                "(s.st_price * post.post_quantity) AS sub_total, s.st_category " +
                "FROM place_orders_stock post " +
                "INNER JOIN stock s " +
                "ON post.st_no = s.st_no " +
                "WHERE s.st_category = ?;";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // DTO 객체 생성 및 값 설정
                    PlaceOnOrdersHistoryDTO user = new PlaceOnOrdersHistoryDTO();
                    user.setPoNo(rs.getInt("po_no"));
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setPostQuantity(rs.getInt("post_quantity"));
                    user.setStCategory(rs.getInt("st_category")); // 데이터 타입 확인 필요

                    list.add(user);
                }
            }
        } catch (SQLException e) {
            System.out.println("오류 발생: " + e.getMessage());
        }
        return list;
    }
    public void DeleteOrderHistory2(Connection con, PlaceOrdersStockVO vo) {
        String sql = "delete from place_orders_Stock where po_no = ?;";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vo.getPoNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL문 오류");
        }
    }

    public void DeleteOrderHistory(Connection con, PlaceOrdersVO vo) {
        String sql = "delete from place_orders where po_no = ?;";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vo.getPoNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL문 오류");
        }
    }

    public void UpdateOrderHistory(Connection con, PlaceOrdersStockVO vo) {
        String sql  = "update place_orders_stock set post_quantity = ?" +
                "where st_no = ?;";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vo.getPostQuantity());
            ps.setInt(2, vo.getStNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL문 오류");
        }

    }









}
