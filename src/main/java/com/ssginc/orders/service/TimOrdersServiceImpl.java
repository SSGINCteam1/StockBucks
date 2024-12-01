package com.ssginc.orders.service;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dao.TimOrdersDAO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.util.HikariCPDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
public class TimOrdersServiceImpl implements TimOrdersService {
    TimOrdersDAO timOrdersDAO;
    DataSource dataSource;

    public TimOrdersServiceImpl(){
        timOrdersDAO = new TimOrdersDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
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
        LocalDate orderDate =LocalDate.parse(date, dateTimeFormatter);
        LocalDate now = LocalDate.now();

        Duration diff = Duration.between(orderDate, now);

        return diff.toMinutes();
    }

    @Override
    public int cancelOrderDetails(OrderDetailsDTO orderDetail) {

        // 1. 주문 취소

        // 2. 재료 원복

        // => 각 음료에 사용된 원재료들

        return 0;
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }


    // ---------------------- 4.2. 기간별 주문 내역 조회 ----------------------
    /**
     * 기간별 주문 내역 목록 가져오는 메서드
     * @param year
     * @param month
     * @param day
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByPeriod(int year, int month, int day, int page, int pageSize) {
        if (year < 0 || month < 0 || month > 12 || day < 0 || day > 31) {
            throw new IllegalArgumentException("유효하지 않은 날짜 값입니다.");
        }

        ArrayList<OrdersSelectDTO> orders = new ArrayList<>();

        int offset = (page - 1) * pageSize; // 시작 위치

        try(Connection conn = dataSource.getConnection()){

            if (year != 0 && month != 0 && day != 0){
                orders = timOrdersDAO.selectOrderListByDay(conn, year, month, day, pageSize, offset);
            } else if (year != 0 && month != 0){
                orders = timOrdersDAO.selectOrderListByMonth(conn, year, month, pageSize, offset);
            } else if (year != 0){
                orders = timOrdersDAO.selectOrderListByYear(conn, year, pageSize, offset);
            } else {
                throw new IllegalArgumentException("유효하지 않은 일자값입니다");
            }


        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return orders;
    }
    

    /**
     * 기간별 주문 내역 개수 가져오는 메서드
     * @param year
     * @param month
     * @param day
     * @return
     */
    @Override
    public int selectOrdersListRownumByPeriod(int year, int month, int day) {
        if (year < 0 || month < 0 || month > 12 || day < 0 || day > 31) {
            throw new IllegalArgumentException("유효하지 않은 날짜 값입니다.");
        }

        int result = 0;

        try(Connection conn = dataSource.getConnection()){

            if (year != 0 && month != 0 && day != 0){
                result = timOrdersDAO.selectOrdersListRownumByDay(conn, year, month, day);
            } else if (year != 0 && month != 0){
                result = timOrdersDAO.selectOrdersListRownumByMonth(conn, year, month);
            } else if (year != 0){
                result = timOrdersDAO.selectOrdersListRownumByYear(conn, year);
            } else {
                throw new IllegalArgumentException("유효하지 않은 일자값입니다");
            }


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
        }catch (Exception e){
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public int selectUsersListRownumByUsersName(String userName) {
        int res = 0;

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectUsersListRownumByUsersName(conn, userName);
        } catch (Exception e){
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
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
    public int selectOrdersListRownumByCustom(String startDate, String endDate) {

        int res = 0;

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectOrdersListRownumByCustom(conn, startDate, endDate);
        } catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public int selectOrdersListRownumByCustomAndUsersNo(int usersNo, String startDate, String endDate) {

        int res = 0;

        LocalDate[] localDates = conversionStrToLocalDate(startDate, endDate);
        LocalDate start = localDates[0];
        LocalDate end = localDates[1];

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectOrdersListRownumByCustomAndUsersNo(conn, usersNo, start, end);
        } catch (Exception e){
            e.printStackTrace();
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
        } catch (Exception e){
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * 사용자 정의 주문 내역 목록 가져오는 메서드
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByCustom(String startDate, String endDate, int page, int pageSize) {

        ArrayList<OrdersSelectDTO> orders = null;

        int offset = (page - 1) * pageSize;

        LocalDate[] localDates = conversionStrToLocalDate(startDate, endDate);
        LocalDate start = localDates[0];
        LocalDate end = localDates[1];

        try(Connection conn = dataSource.getConnection()){
            orders = timOrdersDAO.selectOrdersListByCustom(conn,start, end, pageSize, offset);
        } catch (Exception e){
            e.printStackTrace();
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
        } catch (Exception e){
            e.printStackTrace();
        }

        return orderdetail;
    }

    private LocalDate[] conversionStrToLocalDate(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // String을 Date 타입으로 변환하기 위해 Date 형식 지정

        LocalDate startLocalDate = LocalDate.parse(startDate, formatter); // 형식에 맞춰 형변환
        LocalDate endLocalDate = LocalDate.parse(endDate, formatter).plusDays(1); // 시간을 고려하지 않기에 1일을 더 해줘야 종료일을 포함한 범위 검색 가능

        return new LocalDate[]{startLocalDate, endLocalDate};
    }


    // =================================== 5. 품목 판매 중지 ===================================
}
