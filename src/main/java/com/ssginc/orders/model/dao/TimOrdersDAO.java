package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.OptionsDTO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.orders.model.dto.ProductsDTO;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimOrdersDAO {
    /**
     * 전체 주문 내역 조회
     * @param conn
     * @return
     */
    public ArrayList<OrdersSelectDTO> selectOrderList(Connection conn, int offset) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL " +
                "FROM ORDERS " +
                "JOIN USERS USING (USERS_NO) " +
                "ORDER BY ORDERS_NO " +
                "LIMIT 9 OFFSET ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offset);

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

    // 년도별 주문 내역 목록 조회
    public ArrayList<OrdersSelectDTO> selectOrderListByYear(Connection conn, String year) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL FROM ORDERS WHERE YEAR(ORDERS_DATE) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, year);
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
    public ArrayList<OrdersSelectDTO> selectOrderListByMonth(Connection conn, String year, String month) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL FROM ORDERS WHERE YEAR(ORDERS_DATE) = ? " +
                                            "AND MONTH(ORDERS_DATE) = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, year);
            ps.setString(2, month);

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
    public ArrayList<OrdersSelectDTO> selectOrderListByDay(Connection conn, String year, String month, String day) {
        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        String sql = "SELECT ORDERS_NO, ORDERS_DATE, USERS_NAME, ORDERS_TOTAL " +
                    "FROM ORDERS " +
                    "WHERE YEAR(ORDERS_DATE) = ? " +
                                                "AND MONTH(ORDERS_DATE) = ?" +
                                                "AND DAY(ORDERS_DATE) = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, year);
            ps.setString(2, month);
            ps.setString(3, day);

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
    
    // 쿼리문 결과를 OrdersSelectDTO 객체로 매핑해주는 메서드
    private OrdersSelectDTO mapToOrdersSelectDTO(ResultSet rs) throws SQLException {
        return OrdersSelectDTO.builder()
                .orderNo(rs.getInt("orders_no"))
                .orderDate(rs.getString("orders_date"))
                .userName(rs.getString("users_name"))
                .totalPrice(rs.getInt("orders_total"))
                .build();
    }

    public int selectOrdersAllRownum(Connection conn) {

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

    public OrderDetailsDTO selectOrdersDetails(Connection conn, int orderNo) {
        List<ProductsDTO> products = new ArrayList<>(); // 제품 리스트

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
}
