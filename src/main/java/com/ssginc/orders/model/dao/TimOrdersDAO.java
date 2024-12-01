package com.ssginc.orders.model.dao;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dto.OptionsDTO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.orders.model.dto.ProductsDTO;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimOrdersDAO {

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

        } catch (SQLException e) {
            e.printStackTrace();
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

        } catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    // ---------------------- 4.2. 기간별 주문 내역 조회 ----------------------
    // 년도별 주문 내역 목록 조회
    public ArrayList<OrdersSelectDTO> selectOrderListByYear(Connection conn, int year, int pageSize, int offset) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL 
                FROM ORDERS 
                JOIN (USERS) USING (USERS_NO)
                WHERE YEAR(ORDERS_DATE) = ?
                ORDER BY ORDERS_DATE
                LIMIT ? OFFSET ?
                 """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // 월별 주문 내역 목록 조회
    public ArrayList<OrdersSelectDTO> selectOrderListByMonth(Connection conn, int year, int month, int pageSize, int offset) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL 
                FROM ORDERS 
                JOIN (USERS) USING (USERS_NO)
                WHERE YEAR(ORDERS_DATE) = ?
                AND MONTH(ORDERS_DATE) = ?
                ORDER BY ORDERS_DATE
                LIMIT ? OFFSET ?
                 """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    orders.add(mapToOrdersSelectDTO(rs));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return orders;
    }
    
    // 일자별 주문 내역 목록 조회
    public ArrayList<OrdersSelectDTO> selectOrderListByDay(Connection conn, int year, int month, int day, int pageSize, int offset) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = """
                SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL 
                FROM ORDERS 
                JOIN (USERS) USING (USERS_NO)
                WHERE YEAR(ORDERS_DATE) = ?
                AND MONTH(ORDERS_DATE) = ?
                AND DAY(ORDERS_DATE) = ?
                ORDER BY ORDERS_DATE
                LIMIT ? OFFSET ?
                 """;

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, day);
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                        orders.add(mapToOrdersSelectDTO(rs));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;
    }

    public int selectOrdersListRownumByDay(Connection conn, int year, int month, int day) {
        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO) 
                FROM ORDERS
                WHERE YEAR(ORDERS_DATE) = ?
                    AND MONTH(ORDERS_DATE) = ?
                    AND DAY(ORDERS_DATE) = ?
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, day);

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

    public int selectOrdersListRownumByMonth(Connection conn, int year, int month) {
        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO) 
                FROM ORDERS
                WHERE YEAR(ORDERS_DATE) = ?
                    AND MONTH(ORDERS_DATE) = ?
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);

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

    public int selectOrdersListRownumByYear(Connection conn, int year) {
        int res = 0;

        String sql = """
                SELECT COUNT(ORDERS_NO) 
                FROM ORDERS
                WHERE YEAR(ORDERS_DATE) = ?
                """;
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);

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
        } catch (Exception e){
            e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e){
            e.printStackTrace();
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
        } catch (Exception e){
            e.printStackTrace();
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

        } catch (Exception e){
            e.printStackTrace();
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

        } catch (Exception e){
            e.printStackTrace();
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
        } catch (Exception e){
            e.printStackTrace();
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;

    }


    // ---------------------- 4.5. 주문 내역 조회 유틸 메서드 ----------------------
    
    // 주문 세부 객체 select
    public OrderDetailsDTO selectOrdersDetails(Connection conn, int orderNo) {
        List<ProductsDTO> products = new ArrayList<>(); // 제품 리스트

        //  자바17부터는 """ 로 텍스트 블록 정의 가능
        String sql = """
        SELECT opd.ord_prd_no, opd.p_no, opd.opd_quantity, p.p_name, p.p_price, oopt.opt_no, oopt.oropt_name, oopt.oropt_price, oopt.oropt_quantity
        FROM ORDERS o
        JOIN ORDERS_PRD opd USING (orders_no)
        LEFT JOIN ORDERS_OPT oopt USING (ord_prd_no)
        LEFT JOIN products p ON p.p_no = opd.p_no
        WHERE o.ORDERS_NO = ?
        ORDER BY opd.p_no, oopt.opt_no;
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderNo); // 동적 파라미터 설정

            try (ResultSet rs = ps.executeQuery()) {
                ProductsDTO currentProduct = null;

                int prevOrdPrdNo = -1; // 이전 제품 번호를 저장

                while (rs.next()) {
                    int currOrdPrdNo = rs.getInt("ord_prd_no");

                    // 새로운 제품을 만나면 이전 제품 저장 및 초기화
                    if (currOrdPrdNo != prevOrdPrdNo) {
                        if (currentProduct != null) {
                            products.add(currentProduct); // 이전 제품 저장
                        }

                        // 새로운 제품 생성
                        currentProduct = ProductsDTO.builder()
                                .pno(rs.getInt("p_no"))
                                .pname(rs.getString("p_name"))
                                .quantity(rs.getInt("opd_quantity"))
                                .price(rs.getInt("p_price"))
                                .options(new ArrayList<>()) // 옵션 리스트 초기화
                                .build();

                        prevOrdPrdNo = currOrdPrdNo;
                    }

                    // 옵션 추가 (옵션이 NULL인 경우 건너뜀)
                    int optNo = rs.getInt("opt_no");
                    if (!rs.wasNull()) { // optNo가 NULL이 아닌 경우에만 추가
                        OptionsDTO option = OptionsDTO.builder()
                                .optNo(optNo)
                                .optName(rs.getString("oropt_name"))
                                .price(rs.getInt("oropt_price"))
                                .quantity(rs.getInt("oropt_quantity"))
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
            e.printStackTrace(); // 프로덕션 환경에서는 로깅 사용
        }

        // 최종 결과에 반영
        return OrderDetailsDTO.builder()
                .products(products) // 제품 리스트 설정
                .build();
    }

    // 쿼리문 결과를 OrdersSelectDTO 객체로 매핑해주는 메서드
    private OrdersSelectDTO mapToOrdersSelectDTO(ResultSet rs) throws SQLException {
        return OrdersSelectDTO.builder()
                .orderNo(rs.getInt("orders_no"))
                .orderDate(rs.getString("orders_date"))
                .userName(rs.getString("users_name"))
                .totalPrice(rs.getInt("orders_total"))
                .build();
    }

    
    
    // =================================== 5. 품목 판매 중지 ===================================
    
    
}
