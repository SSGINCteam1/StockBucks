package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.DongOrdersDAO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DongOrdersServiceImpl implements DongOrdersService {
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
        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;
    }

    // 기간별 주문 내역 목록 가져오는 메서드
    @Override
    public List<OrdersSelectDTO> selectOrderListByPeriod(String period, String date) {
        List<OrdersSelectDTO> orders = null;

        try(Connection conn = dataSource.getConnection()){

            switch (period) {
                case "년도별":
                    orders = dongOrdersDAO.selectOrderListByYear(conn, date);
                    break;
                case "월별" :
                    orders = dongOrdersDAO.selectOrderListByMonth(conn, date);
                    break;
                case "일자별" :
                    orders = dongOrdersDAO.selectOrderListByDay(conn, date);
            }

        } catch (Exception e){
            e.printStackTrace();
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
