package com.ssginc.orders.model.dao;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimOrdersDAO {

    // =================================== 3. 주문 취소 ===================================

    public int deleteOrdersForCancelOrder(Connection conn, int orderNo) {
        int res = 0;
        String sql = """
                DELETE FROM orders
                WHERE orders_no = ?;
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderNo);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int deleteOrdersOptForCancelOrder(Connection conn, int orderNo) {
        int res = 0;
        String sql = """
                DELETE FROM orders_opt
                    WHERE ord_prd_no IN (
                        SELECT ord_prd_no
                        FROM orders_prd
                        WHERE orders_no = ?
                    )
                    """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderNo);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public int deleteOrdersPrdForCancelOrder(Connection conn, int orderNo) {
        int res = 0;
        String sql = """
                DELETE FROM orders_prd
                WHERE orders_no = ?;
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderNo);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int deleteOrdersStockForCancelOrder(Connection conn, int orderNo) {
        int res = 0;
        String sql = """
                DELETE FROM orders_stock
                WHERE orders_no = ?
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderNo);
            res = ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int updateStockForRestore(Connection conn, int stockNo, int stockConsumption) {

        int res = 0;

        String sql = """
                UPDATE stock
                SET st_quantity = st_quantity + ?
                WHERE st_no = ?
                    """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, stockConsumption);
            ps.setInt(2, stockNo);

            res = ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public List<ConsumptionDTO> selectProductConsumptionList(Connection conn, int productNo) {

        List<ConsumptionDTO> res = new ArrayList<>();

        String sql = """
                SELECT st_no, pst_consume
                FROM prd_stock
                JOIN stock s using (st_no)
                WHERE p_no = ?
                ORDER BY st_no
                    """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(ConsumptionDTO.builder()
                                    .stockNo(rs.getInt("st_no"))
                                    .consumption(rs.getInt("pst_consume"))
                                .build()
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }


    public List<ConsumptionDTO> selectOptConsumptionList(Connection conn, int optNo) {

        List<ConsumptionDTO> res = new ArrayList<>();

        String sql = """
                SELECT st_no, opt_consume
                FROM orders_opt o
                JOIN opt USING (opt_no)
                JOIN opt_category USING (category_no)
                JOIN stock USING (st_no)
                WHERE o.opt_no = ?
                ORDER BY st_no;
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, optNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(ConsumptionDTO.builder()
                            .stockNo(rs.getInt("st_no"))
                            .consumption(rs.getInt("opt_consume"))
                            .build()
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;

    }



    // =================================== 4. 주문 내역 조회 ===================================
    // ---------------------- 4.1. 전체 주문 내역 조회 ----------------------
    /**
     * 전체 주문 내역 조회
     * @param conn
     * @return
     */
    public ArrayList<OrdersSelectDTO> selectOrderList(Connection conn, int pageSize, int offset) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL " +
                "FROM ORDERS " +
                "JOIN USERS USING (USERS_NO) " +
                "ORDER BY ORDERS_NO " +
                "LIMIT ? OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }
    public int selectOrdersListRownumAll(Connection conn) {

        int res = 0;

        String sql = "SELECT COUNT(ORDERS_NO) FROM ORDERS";

        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                res = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    // ---------------------- 4.2. 기간별 주문 내역 조회 ----------------------

    // 년도별 주문 내역 목록 조회

    public ArrayList<OrdersSelectDTO> selectOrderListByPeriod(Connection conn, String start, String end, int pageSize, int offset) {

        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL 
                FROM ORDERS 
                JOIN (USERS) USING (USERS_NO)
                WHERE ORDERS_DATE BETWEEN ? AND ?
                ORDER BY ORDERS_DATE
                LIMIT ? OFFSET ?
                 """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start);
            ps.setString(2, end);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }

        } catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("유효하지 않은 날짜 형식입니다. (형식: yyyy-MM-dd)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;

    }

    public int selectOrdersListRownumByPeriod(Connection conn, String start, String end) {
        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO) 
                FROM ORDERS
                WHERE ORDERS_DATE BETWEEN ? AND ?
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start);
            ps.setString(2, end);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = rs.getInt(1);
                }
            }

        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("유효하지 않은 날짜 형식입니다. (형식: yyyy-MM-dd)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }



    // ---------------------- 4.3. 유저별 주문 내역 조회 ----------------------
    public ArrayList<UsersDTO> selectUsersListByUsersName(Connection conn, String username, int pageSize, int offset) {
        ArrayList<UsersDTO> res = new ArrayList<>();

        String sql = """
                    SELECT USERS_NO, USERS_NAME, USERS_BIRTH
                    FROM USERS
                    WHERE USERS_NAME = ?
                    ORDER BY USERS_BIRTH
                    LIMIT ? OFFSET ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            try(ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    res.add(UsersDTO.builder()
                                    .usersNo(rs.getInt("users_no"))
                                    .usersName(rs.getString("users_name"))
                                    .usersBirth(rs.getString("users_birth"))
                                .build()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public int selectUsersListRownumByUsersName(Connection conn, String userName) {
        int res = 0;

        String sql = """
                    SELECT COUNT(USERS_NO)
                    FROM USERS
                    WHERE USERS_NAME = ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, userName);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public int selectOrdersListRownumByUsers(Connection conn, int usersNo) {
        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO)
                FROM ORDERS
                WHERE USERS_NO = ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, usersNo);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public ArrayList<OrdersSelectDTO> selectOrdersListByUsers(Connection conn,  int usersNo, int pageSize, int offset) {

        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL
                FROM ORDERS
                JOIN USERS U USING (USERS_NO)
                WHERE U.USERS_NO = ? 
                ORDER BY ORDERS_NO
                LIMIT ? OFFSET ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, usersNo);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;

    }



    // ---------------------- 4.4. 사용자 정의 주문 내역 조회 ----------------------
    public int selectOrdersListRownumByCustom(Connection conn, String startDate, String endDate) {

        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO)
                FROM ORDERS
                WHERE ORDERS_DATE >= ? AND ORDERS_DATE <= ?
               """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;

    }

    public ArrayList<OrdersSelectDTO> selectOrdersListByCustom(Connection conn, LocalDate start, LocalDate end, int pageSize, int offset) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                    SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL
                    FROM ORDERS
                    JOIN USERS U USING (USERS_NO)
                    WHERE ORDERS_DATE >= ? AND ORDERS_DATE <= ?
                    LIMIT ? OFFSET ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;

    }

    public int selectOrdersListRownumByCustomAndUsersNo(Connection conn, int usersNo, LocalDate start, LocalDate end) {

        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO)
                FROM ORDERS
                WHERE USERS_NO = ?
                AND (ORDERS_DATE >= ? AND ORDERS_DATE <= ?)
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, usersNo);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;

    }

    public ArrayList<OrdersSelectDTO> selectOrderListByCustomAndUsersNo(Connection conn, int usersNo, LocalDate start, LocalDate end, int pageSize, int offset) {

        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL
                FROM ORDERS
                JOIN USERS U USING (USERS_NO)
                WHERE USERS_NO = ?
                AND (ORDERS_DATE >= ? AND ORDERS_DATE <= ?)
                LIMIT ? OFFSET ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, usersNo);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;

    }

    // ---------------------- 4.5. 주문 내역 조회 유틸 메서드 ----------------------

    // 주문 세부 객체 select
    public OrderDetailsDTO selectOrdersDetails(Connection conn, int orderNo) {
        List<ProductsDTO> products = new ArrayList<>(); // 제품 리스트

        //  자바17부터는 """ 로 텍스트 블록 정의 가능
        String sql = """
       WITH combined_data AS
        (SELECT
               opd.ord_prd_no AS "ord_prd_no",
                opd.p_no AS "p_no",
                p.p_name AS "p_name",
                p.p_price AS "p_price",
                opd.opd_quantity AS "opd_quantity",
                oopt.opt_no AS "opt_no",
                oopt.oropt_name AS "opt_name",
                oopt.oropt_price AS "opt_price",
                oopt.oropt_quantity AS "opt_quantity",
                1 AS "delimiter"
         FROM ORDERS o
         LEFT JOIN ORDERS_PRD opd ON o.orders_no = opd.orders_no
         LEFT JOIN ORDERS_OPT oopt ON opd.ord_prd_no = oopt.ord_prd_no
         LEFT JOIN products p ON p.p_no = opd.p_no
         WHERE o.ORDERS_NO = ?
        
        UNION
        
        SELECT
               0 AS "ord_prd_no",
               ost.st_no AS "p_no",
               s.st_name AS "p_name",
               s.st_price AS "p_price",
               ost.ost_quantity AS "opd_quantity",
               NULL AS "opt_no",
               NULL AS "opt_name",
               NULL AS "opt_price",
               NULL AS "opt_quantity",
               2 AS "delimiter"
        FROM ORDERS o
        LEFT JOIN ORDERS_STOCK ost ON o.orders_no = ost.orders_no
        JOIN stock s ON ost.st_no = s.st_no
        WHERE o.orders_no = ?)
        
        SELECT
        	ROW_NUMBER() OVER(ORDER BY ord_prd_no, p_no) AS "rownum",
             ord_prd_no,
        	 p_no,
        	 p_name,
        	 p_price,
        	 opd_quantity,
        	 opt_no,
        	 opt_name,
        	 opt_price,
        	 opt_quantity,
        	 delimiter
        FROM combined_data
        ORDER BY rownum
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderNo); // 동적 파라미터 설정
            ps.setInt(2, orderNo); // 동적 파라미터 설정

            try (ResultSet rs = ps.executeQuery()) {
                ProductsDTO currentProduct = null;

                int prevOrdPrdNo = -1; // 이전 제품 번호를 저장
                int prevRownum = -1;

                while (rs.next()) {
                    int currRownum = rs.getInt("rownum");
                    int currOrdPrdNo = rs.getInt("ord_prd_no");

                    // 새로운 제품을 만나면 이전 제품 저장 및 초기화
                    if (currOrdPrdNo != prevOrdPrdNo || (currOrdPrdNo == 0 && currRownum != prevRownum)) {
                        if (currentProduct != null) {
                            products.add(currentProduct); // 이전 제품 저장
                        }

                        // 새로운 제품 생성
                        currentProduct = ProductsDTO.builder()
                                .isBeverage(rs.getInt("delimiter"))
                                .pno(rs.getInt("p_no"))
                                .pname(rs.getString("p_name"))
                                .quantity(rs.getInt("opd_quantity"))
                                .price(rs.getInt("p_price"))
                                .options(new ArrayList<>()) // 옵션 리스트 초기화
                                .build();

                        prevOrdPrdNo = currOrdPrdNo;
                        prevRownum = currRownum;
                    }

                    // 옵션 추가 (옵션이 NULL인 경우 건너뜀)
                    int optNo = rs.getInt("opt_no");
                    if (!rs.wasNull()) { // optNo가 NULL이 아닌 경우에만 추가
                        OptionsDTO option = OptionsDTO.builder()
                                .optNo(optNo)
                                .optName(rs.getString("opt_name"))
                                .price(rs.getInt("opt_price"))
                                .quantity(rs.getInt("opt_quantity"))
                                .build();

                        currentProduct.getOptions().add(option); // 현재 제품의 옵션 리스트에 추가
                    }
                }

                // 마지막 제품 저장
                if (currentProduct != null) {
                    products.add(currentProduct);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 최종 결과에 반영
        return OrderDetailsDTO.builder()
                .orderNo(orderNo)
                .products(products) // 제품 리스트 설정
                .build();
    }


    // 쿼리문 결과를 OrdersSelectDTO 객체로 매핑해주는 메서드
    public OrdersSelectDTO mapToOrdersSelectDTO(ResultSet rs) throws SQLException {
        return OrdersSelectDTO.builder()
                .orderNo(rs.getInt("orders_no"))
                .orderDate(rs.getString("orders_date"))
                .userName(rs.getString("users_name"))
                .totalPrice(rs.getInt("orders_total"))
                .build();
    }




    // =================================== 5. 품목 판매 중지 ===================================
    public int updateProductsIsActive(Connection conn, int state, int pno) {

        int res = 0;

        String sql = """
                UPDATE products 
                SET p_state = ? 
                WHERE p_no = ?
                """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, state);
            ps.setInt(2, pno);

            res = ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }



    // =================================== 1. 품목 조회 ===================================
    public ArrayList<ProductsDTO> selectEtcListAll(Connection conn, int type, boolean isOrder, int pageSize, int offset) {

        ArrayList<ProductsDTO> stocks = new ArrayList<>();

        String sql = null;

        if (isOrder) {
            sql = """
                SELECT st_no, st_name, st_price, st_state
                FROM stock
                WHERE st_owner = 1
                AND st_category = ?
                AND st_state = 1
                """;
        } else {
            sql = """
                SELECT st_no, st_name, st_price, st_state
                FROM stock
                WHERE st_owner = 1
                AND st_category = ?
                """;
        }

        sql += " ORDER BY st_no LIMIT ? OFFSET ?";


        try(PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, type);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stocks.add(ProductsDTO.builder()
                            .pno(rs.getInt("st_no"))
                            .pname(rs.getString("st_name"))
                            .price(rs.getInt("st_price"))
                            .isActive(rs.getInt("st_state") == 1)
                            .build());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stocks;
    }

    public int selectEtcListRownumAll(Connection conn ,int type, boolean isOrder) {

        int res = 0;

        String sql = null;

        if (isOrder) {
            sql = """
                SELECT COUNT(st_no)
                FROM stock
                WHERE st_owner = 1
                AND st_category = ?
                AND st_state = 1
                """;
        } else {
            sql = """
                SELECT COUNT(st_no)
                FROM stock
                WHERE st_owner = 1
                AND st_category = ?
                """;
        }

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, type);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = rs.getInt(1);
                }
            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return res;
    }


    public ArrayList<PrdCgDTO> selectPrdCgListAll(Connection conn, int pageSize, int offset) {
        ArrayList<PrdCgDTO> selectProducts = new ArrayList<>();

        String sql = "SELECT prdcg_no, prdcg_name FROM prdcg WHERE prdcg_state = 1 ORDER BY prdcg_no LIMIT ? OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    selectProducts.add(
                            PrdCgDTO.builder()
                                    .prdCgNo(rs.getInt("prdcg_no"))
                                    .prdCgName(rs.getString("prdcg_name"))
                                    .build()
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return selectProducts;
    }

    public int selectPrdCgListRownumAll(Connection conn) {

        int res = 0;

        String sql = "SELECT COUNT(prdcg_no) FROM prdcg WHERE prdcg_state = 1";

        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            if (rs.next()) {
                res = rs.getInt(1);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return res;
    }


    public ArrayList<ProductsDTO> selectProductsListByPrdcgNo(Connection conn, int prdcgNo, boolean isOrder, int pageSize, int offset) {
        ArrayList<ProductsDTO> selectProducts = new ArrayList<>();

        String sql = null;

        if (!isOrder) {
            sql ="SELECT p_no, p_name, p_price, p_state FROM products where prdcg_no = ?";
        } else {
            sql = "SELECT p_no, p_name, p_price, p_state FROM products where prdcg_no = ? and p_state = 1";
        }

        sql += " ORDER BY p_no LIMIT ? OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, prdcgNo);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    selectProducts.add(
                            ProductsDTO.builder()
                                    .pno(rs.getInt("p_no"))
                                    .pname(rs.getString("p_name"))
                                    .price(rs.getInt("p_price"))
                                    .isActive(rs.getInt("p_state") == 1)
                                    .build()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return selectProducts;
    }

    public int selectProductsListRownumByPrdcgNo(Connection conn, int prdcgNo, boolean isOrder) {

        int res = 0;

        String sql = null;

        if (!isOrder) {
            sql ="SELECT COUNT(p_no) FROM products where prdcg_no = ?";
        } else {
            sql = "SELECT COUNT(p_no) FROM products where prdcg_no = ? and p_state = 1";
        }

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, prdcgNo);

            ps.executeQuery();

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = rs.getInt(1);
                }
            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return res;
    }

    // =================================== 2. 품목 주문 ===================================
    
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

        }  catch (SQLException e) {
            throw new RuntimeException(e);
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

        }  catch (SQLException e) {
            throw new RuntimeException(e);
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

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;

    }
}
