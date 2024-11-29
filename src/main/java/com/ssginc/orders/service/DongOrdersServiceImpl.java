package com.ssginc.orders.service;

import com.ssginc.Main;
import com.ssginc.orders.model.dao.DongOrdersDAO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.util.HikariCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DongOrdersServiceImpl implements DongOrdersService {
    private static final Logger log = LoggerFactory.getLogger(DongOrdersServiceImpl.class);

    DongOrdersDAO dongOrdersDAO;
    DataSource dataSource;

    public DongOrdersServiceImpl(){
        dongOrdersDAO = new DongOrdersDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 주문 내역 목록 가져오는 메서드
    @Override
    public List<OrdersSelectDTO> selectOrderList() {
        List<OrdersSelectDTO> orders = null;

        try(Connection conn = dataSource.getConnection()){
            orders = dongOrdersDAO.selectOrderDetailsList(conn);
        } catch (SQLException e){
            log.error("데이터베이스 연결 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return orders;
    }

    // 기간별 주문 내역 목록 가져오는 메서드
    @Override
    public List<OrdersSelectDTO> selectOrderListByPeriod(String period, String date) {
        List<OrdersSelectDTO> orders = null;
        String[] dates = date.split("-");
        String year = dates[0];
        String month = (dates.length > 1) ? dates[1] : null;
        String day = (dates.length > 2) ? dates[2] : null;

        try(Connection conn = dataSource.getConnection()){

            switch (period) {
                case "년도별":
                    orders = dongOrdersDAO.selectOrderListByYear(conn, date);
                    break;
                case "월별" :
                    orders = dongOrdersDAO.selectOrderListByMonth(conn, year, month);
                    break;
                case "일자별" :
                    orders = dongOrdersDAO.selectOrderListByDay(conn, year, month, day);
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
    public List<OrdersSelectDTO> selectOrderListByUserNo(String user) {
        return null;
    }

    // 사용자 정의 주문 내역 목록 가져오는 메서드
    @Override
    public List<OrdersSelectDTO> selectOrderListByCustom(String startDate, String endDate) {
        return null;
    }
}
