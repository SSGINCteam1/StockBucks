package com.ssginc.orders.service;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dao.TimOrdersDAO;
import com.ssginc.orders.model.dto.*;
import com.ssginc.util.HikariCPDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TimOrdersServiceImpl implements TimOrdersService {
    TimOrdersDAO timOrdersDAO;
    DataSource dataSource;

    public TimOrdersServiceImpl(){
        timOrdersDAO = new TimOrdersDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }


    // =================================== 1. 품목 조회 ===================================


    @Override
    public ArrayList<ProductsDTO> selectEtcListAll(int purpose, int type) {
        ArrayList<ProductsDTO> stocks = null;

        boolean isOrder = (purpose == 2);

        try(Connection conn = dataSource.getConnection()){
            stocks = timOrdersDAO.selectEtcListAll(conn, type, isOrder);
        } catch (Exception e){
            e.printStackTrace();
        }

        return stocks;
    }

    @Override
    public ArrayList<PrdCgDTO> selectPrdCgListAll() {

        ArrayList<PrdCgDTO> prdCgs = null;

        try(Connection conn = dataSource.getConnection()) {
            prdCgs = timOrdersDAO.selectPrdCgListAll(conn);
        } catch (Exception e){
            e.printStackTrace();
        }

        return prdCgs;
    }

    @Override
    public int insertOrders(List<ProductsDTO> products, int usersNo) {

        int res = 1;

        int totalQuantity = products.size();

        int totalPrice = 0;

        int prdPrice = 0;

        for (ProductsDTO product : products) {
            prdPrice = 0;
            if (product.getOptions() != null){
                for (OptionsDTO opt : product.getOptions()) {
                    int optQuantity = opt.getQuantity() == 0 ? 1 : opt.getQuantity();
                    prdPrice += opt.getPrice() * optQuantity;
                }
            }
            prdPrice += product.getPrice() * product.getQuantity();
            totalPrice += prdPrice;
        }

        Connection conn = null;

        try{
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            // 소모량 구하기
            Map<Integer, Integer> consumptionMap = new HashMap<>(); // key : st_no / value : 소모량

            List<ConsumptionDTO> consumptions = null;

            // insert 한 orders 테이블의 기본키 반환
            int ordersKey = timOrdersDAO.insertOrders(conn, totalQuantity, totalPrice, usersNo);

            if(ordersKey <= 0){
                throw new Exception("주문 생성에 실패했습니다.");
            }

            for (ProductsDTO product : products) {

                if (product.getIsBeverage() == 1){
                    int ordPrdKey = timOrdersDAO.insertOrdersPrd(conn,ordersKey, product.getQuantity(), product.getPno());

                    if (ordPrdKey <= 0) {
                        throw new Exception("주문 상품 생성에 실패했습니다.");
                    }

                    if (product.getOptions() != null){
                        for (OptionsDTO opt : product.getOptions()) {

                            int optRes = timOrdersDAO.insertOrdersOpt(conn, ordPrdKey, opt.getOptNo(), opt.getPrice(), opt.getQuantity(), opt.getOptName());

                            if (optRes <= 0) {
                                throw new Exception("옵션 생성에 실패했습니다.");
                            }

                            // 옵션 제조  소모량
                            consumptions = timOrdersDAO.selectOptConsumptionList(conn,opt.getOptNo());
                            for (ConsumptionDTO c : consumptions) {
                                consumptionMap.put(c.getStockNo(), consumptionMap.getOrDefault(consumptionMap.get(c.getStockNo()), 0) + c.getConsumption() * opt.getQuantity());
                            }
                        }

                    }

                    // 음료 제조 소모량
                    consumptions = timOrdersDAO.selectProductConsumptionList(conn, product.getPno()); // 음료 제조  소모량
                    for (ConsumptionDTO c : consumptions) {
                        consumptionMap.put(c.getStockNo(), consumptionMap.getOrDefault(consumptionMap.get(c.getStockNo()), 0) + c.getConsumption() * product.getQuantity());
                    }

                } else if (product.getIsBeverage() == 2){
                    consumptionMap.put(product.getPno(), consumptionMap.getOrDefault(consumptionMap.get(product.getPno()), 0) + product.getQuantity());

                    int stockRes = timOrdersDAO.insertOrdersStock(conn, ordersKey, product.getPno(), product.getQuantity());

                    if (stockRes <= 0) {
                        throw new Exception("재고 주문 생성에 실패했습니다.");
                    }
                }
            }


            for (int key : consumptionMap.keySet()) {
                int stockConsumption = consumptionMap.get(key);

                int stockRes = timOrdersDAO.updateStockForOrders(conn, key, stockConsumption);

                if (stockRes <= 0) {
                    throw new Exception("재고 업데이트에 실패했습니다.");
                }
            }

            conn.commit();

        } catch (Exception e){

            if (conn != null){
                try {
                    conn.rollback();
                    log.error("트랜잭션 중 오류 발생 = {}", e.getMessage());
                } catch (Exception rollbackEx){
                    log.error("롤백 중 오류 발생 = {}", rollbackEx.getMessage());
                }
            }

            log.error("알 수 없는 에러 발생 = {}", e.getMessage());
            res = 0; // 실패 시 결과값 초기화
        } finally {
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Connection close 실패 = {}", e.getMessage());
                }
            }
        }

        return res;
    }

    @Override
    public ArrayList<ProductsDTO> selectProductsListByPrdcgNo(int prdcgNo, int purpose) {

        boolean isOrder = (purpose == 2);

        ArrayList<ProductsDTO> prds = null;

        try(Connection conn = dataSource.getConnection()) {
            prds = timOrdersDAO.selectProductsListByPrdcgNo(conn, prdcgNo, isOrder);
        } catch (Exception e){
            e.printStackTrace();
        }

        return prds;

    }

    @Override
    public ArrayList<PrdOptDTO> selectPrdOpt(int pNo) {

        ArrayList<PrdOptDTO> selectPrdOpt = new ArrayList<>();

        String sql = """ 
             SELECT p.p_no, o.category_no, o.optcg_name, ot.opt_no, ot.opt_name, ot.opt_price
            FROM products p
            JOIN prd_optcg o ON o.p_no = p.p_no
            JOIN opt_category oc ON o.category_no = oc.category_no
            JOIN opt ot ON ot.category_no = oc.category_no
            WHERE p.p_no = ?
            ORDER BY p.p_no, o.category_no, ot.opt_no
                """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pNo);

            try (ResultSet rs = ps.executeQuery()) {
                int prevCategoryNo = -1; // 이전 카테고리 번호
                PrdOptDTO currentPrdOptDTO = null; // 현재 PrdOptDTO
                ArrayList<PrdOptDetailDTO> details = null; // 옵션 상세 리스트

                while (rs.next()) {
                    int currCategoryNo = rs.getInt("category_no");

                    // 카테고리가 변경될 때 새로운 PrdOptDTO 생성
                    if (currCategoryNo != prevCategoryNo) {
                        if (currentPrdOptDTO != null) {
                            // 이전 PrdOptDTO에 옵션 리스트를 설정
                            currentPrdOptDTO.setOptionDetails(details);
                            selectPrdOpt.add(currentPrdOptDTO);
                        }

                        // 새로운 PrdOptDTO 객체 생성
                        currentPrdOptDTO = new PrdOptDTO();
                        currentPrdOptDTO.setPNo(rs.getInt("p_no"));
                        currentPrdOptDTO.setOptCategoryNo(currCategoryNo);
                        currentPrdOptDTO.setOptCategoryName(rs.getString("optcg_name"));

                        // 새로운 옵션 상세 리스트 초기화
                        details = new ArrayList<>();
                    }

                    // PrdOptDetailDTO 생성 및 설정
                    PrdOptDetailDTO detail = new PrdOptDetailDTO();
                    detail.setOptNo(rs.getInt("opt_no"));
                    detail.setOptionName(rs.getString("opt_name"));
                    detail.setOptionPrice(rs.getInt("opt_price"));

                    // 옵션 상세 리스트에 추가
                    details.add(detail);

                    // 이전 카테고리 번호 갱신
                    prevCategoryNo = currCategoryNo;
                }

                // 마지막 PrdOptDTO 객체를 리스트에 추가
                if (currentPrdOptDTO != null) {
                    currentPrdOptDTO.setOptionDetails(details);
                    selectPrdOpt.add(currentPrdOptDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectPrdOpt;

    }
    

    // =================================== 3. 주문 취소 ===================================

    /**
     * 주문 일시와 현재 일시의 분 차이 계산하는 메서드
     * @param date
     * @return
     */
    @Override
    public long getDiffMinOrderDateAndNow(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime orderDate = LocalDateTime.parse(date, dateTimeFormatter);
        LocalDateTime now = LocalDateTime.now();

        Duration diff = Duration.between(orderDate, now);

        return diff.toMinutes();
    }

    @Override
    public int cancelOrderDetails(OrderDetailsDTO orderDetail) {

        int res = 1;

        int orderNo = orderDetail.getOrderNo();

        boolean includeEtc = false;

        Connection conn = null;


        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false); // 오토 커밋 비활성화

            // 1. 주문 취소

            int delOrdOpt = timOrdersDAO.deleteOrdersOptForCancelOrder(conn, orderNo);
            if (delOrdOpt <= 0) {
                throw new Exception("주문 옵션 데이터 삭제 실패했습니다.");
            }
            int delOrdPrd = timOrdersDAO.deleteOrdersPrdForCancelOrder(conn, orderNo);
            if (delOrdPrd <= 0) {
                throw new Exception("주문 음료 데이터 삭제 실패했습니다.");
            }
            for (ProductsDTO prd : orderDetail.getProducts()){
                if (prd.getIsBeverage() == 2){
                    includeEtc = true;
                    break;
                }
            }

            if (includeEtc){
                int delOrdSt = timOrdersDAO.deleteOrdersStockForCancelOrder(conn, orderNo);
                if (delOrdSt <= 0){
                    throw new Exception("주문 재고물품 데이터 삭제 실패했습니다.");
                }
            }

            int delOrder = timOrdersDAO.deleteOrdersForCancelOrder(conn, orderNo);
            if (delOrder <= 0) {
                throw new Exception("주문 데이터 삭제 실패했습니다.");
            }

            // 2. 재료 원복
            Map<Integer, Integer> consumptionMap = new HashMap<>(); // key : st_no / value : 소모량

            List<ConsumptionDTO> consumptions = null;

            for (ProductsDTO prd : orderDetail.getProducts()) {

                if (prd.getIsBeverage() == 1){
                    consumptions = timOrdersDAO.selectProductConsumptionList(conn, prd.getPno()); // 음료 제조  소모량
                    for (ConsumptionDTO c : consumptions) {
                        consumptionMap.put(c.getStockNo(), consumptionMap.getOrDefault(consumptionMap.get(c.getStockNo()), 0) + c.getConsumption() * prd.getQuantity());
                    }
                    for (OptionsDTO opt : prd.getOptions()) {
                        consumptions = timOrdersDAO.selectOptConsumptionList(conn,opt.getOptNo()); // 옵션 제조  소모량
                        for (ConsumptionDTO c : consumptions) {
                            consumptionMap.put(c.getStockNo(), consumptionMap.getOrDefault(consumptionMap.get(c.getStockNo()), 0) + c.getConsumption() * opt.getQuantity());
                        }
                    }
                } else { // 음료 외 상품일시
                    consumptionMap.put(prd.getPno(), consumptionMap.getOrDefault(consumptionMap.get(prd.getPno()), 0) + prd.getQuantity());
                }
            }

            for (int key : consumptionMap.keySet()) {
                int stockConsumption = consumptionMap.get(key);

                int updSt = timOrdersDAO.updateStockForRestore(conn, key, stockConsumption);
                if (updSt <= 0){
                    throw new Exception("재고 업데이트에 실패했습니다.");
                }
            }

            conn.commit();

        } catch (Exception e){

            if (conn != null){
                try {
                    conn.rollback();
                    log.error("트랜잭션 중 오류 발생 = {}", e.getMessage());
                } catch (Exception rollbackEx){
                    log.error("롤백 중 오류 발생 = {}", rollbackEx.getMessage());
                }
            }

            log.error("알 수 없는 에러 발생 = {}", e.getMessage());
            res = 0; // 실패 시 결과값 초기화
        } finally {
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Connection close 실패 = {}", e.getMessage());
                }
            }
        }

        return res;
    }


    // =================================== 4. 주문 내역 조회 ===================================


    // ---------------------- 4.1. 전체 주문 내역 조회 ----------------------

    /**
     * 주문 내역 목록 가져오는 메서드
      */
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderList(int page, int pageSize) {
        ArrayList<OrdersSelectDTO> orders = null;

        int offset = (page - 1) * pageSize; // 페이지 번호에 따라 시작 위치 계산
        
        try(Connection conn = dataSource.getConnection()){
            orders = timOrdersDAO.selectOrderList(conn, pageSize, offset);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return orders;
    }

    /**
     * 모든 주문 내역의 개수를 가져오는 메서드
     * @return
     */
    @Override
    public int selectOrdersListRownumAll() {
        int res = 0;

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectOrdersListRownumAll(conn);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return res;
    }


    // ---------------------- 4.2. 기간별 주문 내역 조회 ----------------------

    /**
     * 기간별 주문 내역 목록 가져오는 메서드
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByPeriod(String startDate, String endDate, int page, int pageSize) {
        ArrayList<OrdersSelectDTO> orders = null;

        try {
            // 날짜 형식 검증 및 변환
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (start.isAfter(end)) {
                throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 앞서야 합니다.");
            }

            // 월별 조회 시 정확한 마지막 날 계산
            if (start.getMonth() == end.getMonth() && startDate.endsWith("-01") && !startDate.equals(endDate)) {
                end = start.withDayOfMonth(start.lengthOfMonth());
            }

            // 페이징 처리
            int offset = (page - 1) * pageSize;

            // DAO 호출
            try (Connection conn = dataSource.getConnection()) {
                // 마지막 날을 포함해 조회하기 위해 1일 추가
                orders = timOrdersDAO.selectOrderListByPeriod(conn, start.toString(), end.plusDays(1).toString(), pageSize, offset);
            }
        } catch (IllegalArgumentException e) {
            log.error("입력값 형식 오류 : {}", e.getMessage(), e);
        } catch (SQLException e) {
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return orders;
    }


    /**
     * 기간별 주문 내역 개수 가져오는 메서드
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public int selectOrdersListRownumByPeriod(String startDate, String endDate) {
        int result = 0;

        try {
            // 날짜 형식 검증 및 변환
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (start.isAfter(end)) {
                throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 앞서야 합니다.");
            }

            // 월별 조회 시 정확한 마지막 날 계산
            if (start.getMonth() == end.getMonth() && startDate.endsWith("-01") && !startDate.equals(endDate)) {
                end = start.withDayOfMonth(start.lengthOfMonth());
            }

            // DAO 호출
            try (Connection conn = dataSource.getConnection()) {
                result = timOrdersDAO.selectOrdersListRownumByPeriod(conn, start.toString(), end.plusDays(1).toString());
            }


        } catch (IllegalArgumentException e) {
            log.error("입력값 형식 오류 : {}", e.getMessage(), e);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return result;
    }


    // ---------------------- 4.3. 유저별 주문 내역 조회 ----------------------
    /**
     * 유저명으로 유저 정보 가져오는 메서드
     * @param username
     * @return
     */
    @Override
    public ArrayList<UsersDTO> selectUsersListByUsersName(String username, int page, int pageSize) {
        ArrayList<UsersDTO> users = null;

        int offset = (page - 1) * pageSize;

        try(Connection conn = dataSource.getConnection()){
            users = timOrdersDAO.selectUsersListByUsersName(conn, username, pageSize, offset);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return users;
    }

    @Override
    public int selectUsersListRownumByUsersName(String userName) {
        int res = 0;

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectUsersListRownumByUsersName(conn, userName);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return res;
    }
    /**
     * 유저별 주문 내역 목록 가져오는 메서드
     * @param usersNo
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public ArrayList<OrdersSelectDTO> selectOrdersListByUsersNo(int usersNo, int page, int pageSize) {

        ArrayList<OrdersSelectDTO> orders = null;

        int offset = (page - 1) * pageSize;

        try(Connection conn = dataSource.getConnection()){
            orders = timOrdersDAO.selectOrdersListByUsers(conn, usersNo, pageSize, offset);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return orders;
    }

    @Override
    public int selectOrdersListRownumByUsers(int usersNo) {

        int res = 0;
        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectOrdersListRownumByUsers(conn, usersNo);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }


    // ---------------------- 4.4. 사용자 정의 주문 내역 조회 ----------------------
    @Override
    public int selectOrdersListRownumByCustomAndUsersNo(int usersNo, String startDate, String endDate) {

        int res = 0;

        LocalDate[] localDates = conversionStrToLocalDate(startDate, endDate);
        LocalDate start = localDates[0];
        LocalDate end = localDates[1];

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectOrdersListRownumByCustomAndUsersNo(conn, usersNo, start, end);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return res;
    }

    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByCustomAndUsersNo(int usersNo, String startDate, String endDate, int page, int pageSize) {

        ArrayList<OrdersSelectDTO> orders = null;

        LocalDate[] localDates = conversionStrToLocalDate(startDate, endDate);
        LocalDate start = localDates[0];
        LocalDate end = localDates[1];

        int offset = (page - 1) * pageSize;

        try(Connection conn = dataSource.getConnection()){
            orders = timOrdersDAO.selectOrderListByCustomAndUsersNo(conn, usersNo, start, end, pageSize, offset);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return orders;
    }


    // ---------------------- 4.5. 주문 내역 조회 공통 메서드 ----------------------

    /**
     * 주문 세부 내역 가져오는 메서드
     * @param orderNo
     * @return
     */
    @Override
    public OrderDetailsDTO selectOrdersDetail(int orderNo) {
        OrderDetailsDTO orderdetail = null;

        try (Connection conn = dataSource.getConnection()){
            orderdetail = timOrdersDAO.selectOrdersDetails(conn, orderNo);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return orderdetail;
    }




    private LocalDate[] conversionStrToLocalDate(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // String을 Date 타입으로 변환하기 위해 Date 형식 지정

        LocalDate startLocalDate = LocalDate.parse(startDate, formatter); // 형식에 맞춰 형변환
        LocalDate endLocalDate = LocalDate.parse(endDate, formatter).plusDays(1); // 시간을 고려하지 않기에 1일을 더 해줘야 종료일을 포함한 범위 검색 가능

        return new LocalDate[]{startLocalDate, endLocalDate};
    }



    // =================================== 5. 품목 판매 중지 ===================================

    @Override
    public int updateProductsIsActive(int pno, boolean isActive) {

        int res = 0;

        int state = isActive ? 0 : 1;

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.updateProductsIsActive(conn, state, pno);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return res;
    }


}
