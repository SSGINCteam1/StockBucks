package com.ssginc.placeonorders.model.dao;

import com.ssginc.placeonorders.model.dto.*;
import com.ssginc.placeonorders.model.vo.PlaceOrdersStockVO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersVO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaceOnOrdersDAO {
    private final DataSource dataSource;

    public PlaceOnOrdersDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // ========================== 1. 재고 조회 ==========================
    // -------------------------- 1.1 지점 재고 전체 조회 --------------------------
    // 지점 재고 전체 조회
    public List<SelectStockListDTO> selectAllStockList() {
        List<SelectStockListDTO> stockList = null;
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
                    SelectStockListDTO dto = new SelectStockListDTO();

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

    // -------------------------- 1.2 지점 재고 카테고리별 조회 --------------------------
    // 지점 재고 카테고리별 조회
    public List<SelectStockListDTO> selectStockListByCategory(int category) {
        List<SelectStockListDTO> stockList = null;
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
                    SelectStockListDTO dto = new SelectStockListDTO();

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

    // -------------------------- 1.3 지점 재고 키워드 검색 --------------------------
    // 지점 재고 키워드 검색
    public List<SelectStockListDTO> selectStockListByKeyword(String searchKeyword) {
        List<SelectStockListDTO> stockList = null;
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
                    SelectStockListDTO dto = new SelectStockListDTO();

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
    // -------------------------- 2.1 장바구니 품목 목록 조회 --------------------------
    // 장바구니에 담은 품목 조회
    public List<SelectBasketListDTO> selectBasketListByUsersNo(int usersNo) {
        List<SelectBasketListDTO> basketList = null;
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
                    SelectBasketListDTO dto = SelectBasketListDTO.builder()
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
    public SelectBasketListDTO selectBasketStockByUsersNoAndStockNo(int usersNo, int stockNo) {
        SelectBasketListDTO dto = null;
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
                    dto = SelectBasketListDTO.builder()
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
    public int deleteBasketStockByStockNo(int usersNo, int selectedBaksetStockNo) {
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
    public InsertPlaceOrdersDTO insertPlaceOrders(Connection con, int totalPrice, int usersNo) {
        int poNo = 0;
        InsertPlaceOrdersDTO dto = null;
        String sql = """
                INSERT INTO place_orders (po_total, users_no) VALUES (?, ?)
                """;

        // DB에 SQL문 전송
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 전달받은 발주총액과 유저번호를 SQL문의 파라미터로 setting
            ps.setInt(1, totalPrice);
            ps.setInt(2, usersNo);

            int res = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                poNo = rs.getInt(1);
            }

            dto = new InsertPlaceOrdersDTO();
            dto.setResult(res);
            dto.setPoNo(poNo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return dto;
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
    public int deletePlaceOrdersBasketByUsersNo(Connection con, int usersNo) {
        int res = 0;
        String sql = """
                DELETE FROM place_orders_basket WHERE users_no = ?
                """;

        // DB에 SQL문 전송
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // 전달받은 유저번호를 SQL문의 파라미터로 setting
            ps.setInt(1, usersNo);

            res = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }

    // ========================== 3. 발주 내역 조회 ==========================
    // -------------------------- 3.1 발주 내역 전체 조회 --------------------------
    // 발주 내역 전체 조회
    public ArrayList<PlaceonOrdersCheckDTO> selectAllOrderableStockChecks(Connection con) {
        ArrayList<PlaceonOrdersCheckDTO> list = new ArrayList<>();
        String sql = "SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, " +
                "(s.st_price * post.post_quantity) AS sub_total, s.st_category, po.po_date, u.users_name " +
                "FROM place_orders_stock post " +
                "INNER JOIN stock s ON post.st_no = s.st_no " +
                "INNER JOIN place_orders po ON post.po_no = po.po_no " +
                "INNER JOIN users u ON po.users_no = u.users_no ";

        try (PreparedStatement ps = con.prepareStatement(sql)){

            try(ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.out.println("오류");
        }

        return list;
    }

    // -------------------------- 3.2 발주 내역 카테고리별 조회 --------------------------
    // 발주 내역 카테고리별 조회
    public ArrayList<PlaceOnOrdersHistoryDTO> HistoryplaceOrdersStockByCategory(Connection con, int category) {
        ArrayList<PlaceOnOrdersHistoryDTO> list = new ArrayList<>();
        String sql = "SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, " +
                "(s.st_price * post.post_quantity) AS sub_total, s.st_category " +
                "FROM place_orders_stock post " +
                "INNER JOIN stock s " +
                "ON post.st_no = s.st_no " +
                "WHERE s.st_category = ? ";
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

    // -------------------------- 3.3 발주 내역 기간별 조회 --------------------------
    // 년도별 주문 내역 목록 조회
    public ArrayList<PlaceonOrdersCheckDTO> PlaceOnOrdersHistoryByYear(Connection conn, int year) {
        ArrayList<PlaceonOrdersCheckDTO> list = new ArrayList<>();
        String sql = """
                SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, (s.st_price * post.post_quantity) AS sub_total, s.st_category, po.po_date, u.users_name
                FROM place_orders_stock post
                INNER JOIN stock s
                ON post.st_no = s.st_no
                INNER JOIN place_orders po
                ON post.po_no = po.po_no
                INNER JOIN users u
                ON po.users_no = u.users_no
                where year(po_date) = ?
                 """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlaceonOrdersCheckDTO user = new PlaceonOrdersCheckDTO();
                    user.setPoNo(rs.getInt("po_no"));
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setPostQuantity(rs.getInt("post_quantity"));
                    user.setStCategory(rs.getInt("st_category"));
                    user.setPoDate(rs.getString("po_date"));
                    user.setUsersName(rs.getString("users_name"));
                    list.add(user);
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 월별 발주 내역 목록 조회
    public ArrayList<PlaceonOrdersCheckDTO> PlaceOnOrdersHistoryByMonth(Connection conn, int month,int year) {
        ArrayList<PlaceonOrdersCheckDTO> list = new ArrayList<>();
        String sql = """
                SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, (s.st_price * post.post_quantity) AS sub_total, s.st_category, po.po_date, u.users_name
                FROM place_orders_stock post
                INNER JOIN stock s
                ON post.st_no = s.st_no
                INNER JOIN place_orders po
                ON post.po_no = po.po_no
                INNER JOIN users u
                ON po.users_no = u.users_no
                where month(po_date) = ?
                and year(po_date) = ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlaceonOrdersCheckDTO user = new PlaceonOrdersCheckDTO();
                    user.setPoNo(rs.getInt("po_no"));
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setPostQuantity(rs.getInt("post_quantity"));
                    user.setStCategory(rs.getInt("st_category"));
                    user.setPoDate(rs.getString("po_date"));
                    user.setUsersName(rs.getString("users_name"));
                    list.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 일자별 발주 내역 목록 조회
    public ArrayList<PlaceonOrdersCheckDTO> PlaceOnOrdersHistoryByDay(Connection conn, int day,int month,int year) {
        ArrayList<PlaceonOrdersCheckDTO> list = new ArrayList<>();
        String sql = """
                SELECT post.po_no, post.st_no, s.st_name, s.st_price, post.post_quantity, (s.st_price * post.post_quantity) AS sub_total, s.st_category, po.po_date, u.users_name
                FROM place_orders_stock post
                INNER JOIN stock s
                ON post.st_no = s.st_no
                INNER JOIN place_orders po
                ON post.po_no = po.po_no
                INNER JOIN users u
                ON po.users_no = u.users_no
                where day(po_date) = ?
                and month(po_date) = ?
                and year(po_date) = ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, day);
            ps.setInt(2, month);
            ps.setInt(3,year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlaceonOrdersCheckDTO user = new PlaceonOrdersCheckDTO();
                    user.setPoNo(rs.getInt("po_no"));
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setPostQuantity(rs.getInt("post_quantity"));
                    user.setStCategory(rs.getInt("st_category"));
                    user.setPoDate(rs.getString("po_date"));
                    user.setUsersName(rs.getString("users_name"));
                    list.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // -------------------------- 3.4 발주 수정 --------------------------
    // 발주 수정
    public int UpdateOrderHistory(Connection con, PlaceOrdersStockVO vo) {
        int res = 0;
        String sql = """
                    update place_orders_stock
                    set post_quantity = ?
                    where st_no = ? and po_no = ?
                    """;
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vo.getPostQuantity());
            ps.setInt(2, vo.getStNo());
            ps.setInt(3, vo.getPoNo());

            res = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL문 오류");
        }

        return res;
    }

    // -------------------------- 3.5 발주 취소 --------------------------
    // 발주 재고물품 테이블에서 삭제
    public void DeleteOrderHistory2(Connection con, PlaceOrdersStockVO vo) {
        String sql = "delete from place_orders_stock where po_no = ?;";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vo.getPoNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL문 오류");
        }
    }

    // 발주 테이블에서 삭제
    public void DeleteOrderHistory(Connection con, PlaceOrdersVO vo) {
        String sql = "delete from place_orders where po_no = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vo.getPoNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL문 오류");
        }
    }

    // ========================== 4. 발주 가능 품목 조회 ==========================
    // -------------------------- 4.1 발주 가능 품목 전체 조회 --------------------------
    // 전체 데이터 조회
    public ArrayList<PlaceonOrdersDTO> selectAllOrderableStocks(Connection con) {
        ArrayList<PlaceonOrdersDTO> list = new ArrayList<>();
        String sql = "select st_no, st_name, st_price, st_quantity, st_category, st_unit " +
                "from stock where st_state = 1 and st_owner = 1 ";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlaceonOrdersDTO user = new PlaceonOrdersDTO();
                    user.setStNo(rs.getInt("st_no"));
                    user.setStName(rs.getString("st_name"));
                    user.setStPrice(rs.getInt("st_price"));
                    user.setStQuantity(rs.getInt("st_quantity"));
                    user.setStCategory(rs.getInt("st_category"));
                    user.setStUnit(rs.getString("st_unit"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stock data with pagination", e);
        }

        return list; // 결과 반환
    }

    // -------------------------- 4.2 발주 가능 품목 카테고리별 조회 --------------------------
    // 특정 카테고리 데이터 조회
    public ArrayList<PlaceonOrdersDTO> selectAllOrderableStocksByCategory(Connection con, int st_category) {
        ArrayList<PlaceonOrdersDTO> list = new ArrayList<>();
        String sql = "select st_no, st_name, st_price, st_quantity, st_category, st_unit " +
                "from stock where st_state = 1 and st_category = ? and st_owner = 1 ";

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

    // -------------------------- 4.3 발주 가능 품목 키워드 검색 --------------------------
    // 검색 데이터 조회
    public ArrayList<PlaceonOrdersDTO> selectAllOrderableStocksByKeyword(Connection con, String keyword) {
        ArrayList<PlaceonOrdersDTO> list = new ArrayList<>();
        String sql = "SELECT st_no, st_name, st_price, st_quantity, st_category, st_unit " +
                "FROM stock " +
                "WHERE st_state = 1 AND st_name LIKE ? AND st_owner = 1 ";

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

    // -------------------------- 4. 장바구니에 품목 담기 --------------------------
    // 장바구니에 품목 담기
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


}
