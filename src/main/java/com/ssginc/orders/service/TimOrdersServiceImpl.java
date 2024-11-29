package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.TimOrdersDAO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.util.HikariCPDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@Slf4j
public class TimOrdersServiceImpl implements TimOrdersService {
    TimOrdersDAO timOrdersDAO;
    DataSource dataSource;

    public TimOrdersServiceImpl(){
        timOrdersDAO = new TimOrdersDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 주문 내역 목록 가져오는 메서드
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderList(int page, int pageSize) {
        ArrayList<OrdersSelectDTO> orders = null;

        int offset = (page - 1) * pageSize; // 페이지 번호에 따라 시작 위치 계산
        
        try(Connection conn = dataSource.getConnection()){
            orders = timOrdersDAO.selectOrderList(conn, offset);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }

        return orders;
    }

    // 기간별 주문 내역 목록 가져오는 메서드
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByPeriod(String period, String date) {
        ArrayList<OrdersSelectDTO> orders = null;
        String[] dates = date.split("-");
        String year = dates[0];
        String month = (dates.length > 1) ? dates[1] : null;
        String day = (dates.length > 2) ? dates[2] : null;

        try(Connection conn = dataSource.getConnection()){

            switch (period) {
                case "년도별":
                    orders = timOrdersDAO.selectOrderListByYear(conn, date);
                    break;
                case "월별" :
                    orders = timOrdersDAO.selectOrderListByMonth(conn, year, month);
                    break;
                case "일자별" :
                    orders = timOrdersDAO.selectOrderListByDay(conn, year, month, day);
                    break;
                default:
                    throw new IllegalArgumentException("유효하지 않은 period 값: " + period);
            }

        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return orders;
    }

    // 유저별 주문 내역 목록 가져오는 메서드
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByUserNo(String user) {
        return null;
    }

    // 사용자 정의 주문 내역 목록 가져오는 메서드
    @Override
    public ArrayList<OrdersSelectDTO> selectOrderListByCustom(String startDate, String endDate) {
        return null;
    }

    @Override
    public int selectOrdersAllRownum() {
        int res = 0;

        try(Connection conn = dataSource.getConnection()){
            res = timOrdersDAO.selectOrdersAllRownum(conn);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

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
}
