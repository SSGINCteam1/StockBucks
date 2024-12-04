package com.ssginc.placeonorders.model.dao;

import com.ssginc.placeonorders.model.dto.HoonSelectBasketListDTO;
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

    // ========================== 1. 재고 조회 ==========================
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

    // ========================== 2. 발주 신청 ==========================
    // -------------------------- 2.1 장바구니 품목 목록 조회
    // 장바구니에 담은 품목 조회
    public List<HoonSelectBasketListDTO> selectBasketListByUsersNo(int usersNo) {
        List<HoonSelectBasketListDTO> basketList = null;
        String sql = """
                SELECT pob.st_no, s.st_name, s.st_price, pob.pob_quantity, (s.st_price * pob.pob_quantity) AS pob_price, s.st_category, s.st_unit
                FROM place_orders_basket pob
                INNER JOIN stock s ON pob.st_no = s.st_no
                INNER JOIN users u ON pob.users_no = u.users_no
                WHERE u.users_no = ?
                """;
        
        // DB와 connection 연결 및 SQL문 전송
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 usersNo를 SQL문의 파라미터로 setting
            ps.setInt(1, usersNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (basketList == null) {
                        basketList = new ArrayList<>();
                    }

                    // 값 전달을 위한 DTO 생성 및 SQL문을 통해 불러온 field의 값들로 DTO setting(빌더 패턴)
                    HoonSelectBasketListDTO dto = HoonSelectBasketListDTO.builder()
                            .stNo(rs.getInt("st_no"))
                            .stName(rs.getString("st_name"))
                            .stPrice(rs.getInt("st_price"))
                            .placeOrdersQuantity(rs.getInt("pob_quantity"))
                            .placeOrdersPrice(rs.getInt("pob_price"))
                            .stCategory(rs.getInt("st_category"))
                            .stUnit(rs.getString("st_unit"))
                            .build();

                    // DTO List에 DTO 추가
                    basketList.add(dto);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return basketList;
    }

    // 유저 번호와 품목 번호로 장바구니에 담은 품목 조회
    public HoonSelectBasketListDTO selectBasketStockByUsersNoAndStockNo(int usersNo, int stockNo) {
        HoonSelectBasketListDTO dto = null;
        String sql = """
                SELECT pob.st_no, s.st_name, s.st_price, pob.pob_quantity, (s.st_price * pob.pob_quantity) AS pob_price, s.st_category, s.st_unit
                FROM place_orders_basket pob
                INNER JOIN stock s ON pob.st_no = s.st_no
                INNER JOIN users u ON pob.users_no = u.users_no
                WHERE u.users_no = ? AND pob.st_no = ?
                """;

        // DB와 connection 연결 및 SQL문 전송
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 usersNo와 stockNo를 SQL문의 파라미터로 setting
            ps.setInt(1, usersNo);
            ps.setInt(2, stockNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 값 전달을 위한 DTO 생성 및 SQL문을 통해 불러온 field의 값들로 DTO setting(빌더 패턴)
                    dto = HoonSelectBasketListDTO.builder()
                            .stNo(rs.getInt("st_no"))
                            .stName(rs.getString("st_name"))
                            .stPrice(rs.getInt("st_price"))
                            .placeOrdersQuantity(rs.getInt("pob_quantity"))
                            .placeOrdersPrice(rs.getInt("pob_price"))
                            .stCategory(rs.getInt("st_category"))
                            .stUnit(rs.getString("st_unit"))
                            .build();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return dto;
    }

    // -------------------------- 2.1.1 장바구니 품목 수정 --------------------------
    // 장바구니 품목 수정
    public int updateBasketStock(Connection con, int usersNo, int selectedBasketStockNo, int inputQuantity) {
        int res = 0;
        String sql = """
                UPDATE place_orders_basket
                SET pob_quantity = ?
                WHERE users_no = ? AND st_no = ?
                """;

        // DB에 SQL문 전송
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 유저번호, 제품번호와 수정할 수량을 SQL문의 파라미터로 setting
            ps.setInt(1, inputQuantity);
            ps.setInt(2, usersNo);
            ps.setInt(3, selectedBasketStockNo);

            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    // -------------------------- 2.1.2 장바구니 품목 삭제 --------------------------
    // 장바구니 품목 삭제
    public int deleteBasketStock(int usersNo, int selectedBaksetStockNo) {
        int res = 0;
        String sql = """
                DELETE FROM place_orders_basket
                WHERE users_no = ? AND st_no = ?
                """;

        // DB와 connection 연결 및 SQL문 전송
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 유저번호, 제품번호를 SQL문의 파라미터로 setting
            ps.setInt(1, usersNo);
            ps.setInt(2, selectedBaksetStockNo);

            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    // -------------------------- 2.2 장바구니 품목 발주 신청 --------------------------
    // 발주 테이블에 추가
    public int insertPlaceOrders(Connection con, int totalPrice, int usersNo) {
        int res = 0;
        String sql = """
                INSERT INTO place_orders (po_total, users_no) VALUES (?, ?)
                """;

        // DB에 SQL문 전송
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 발주총액과 유저번호를 SQL문의 파라미터로 setting
            ps.setInt(1, totalPrice);
            ps.setInt(2, usersNo);

            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    // 발주_재고물품 테이블에 추가
    public int insertPlaceOrdersStock(Connection con, int poNo, int stNo, int placeOrdersQuantity) {
        int res = 0;
        String sql = """
                INSERT INTO place_orders_stock (po_no, st_no, post_quantity) VALUES (?, ?, ?)
                """;

        // DB에 SQL문 전송
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 발주번호, 제품번호, 발주수량을 SQL문의 파라미터로 setting
            ps.setInt(1, poNo);
            ps.setInt(2, stNo);
            ps.setInt(3, placeOrdersQuantity);

            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    // 발주 장바구니 테이블에서 삭제

}
