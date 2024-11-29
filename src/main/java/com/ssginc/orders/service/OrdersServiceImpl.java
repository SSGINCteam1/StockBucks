package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.OrdersDAO;
import com.ssginc.orders.model.vo.OrdersVO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;

public class OrdersServiceImpl implements OrdersService {
    OrdersDAO ordersDAO = new OrdersDAO();
    DataSource dataSource;


    public OrdersServiceImpl(){
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    @Override
    public ArrayList<OrdersVO> selectOrdersList() {
        ArrayList<OrdersVO> list = null;

        try(Connection con = dataSource.getConnection()) {
            list = ordersDAO.selectOrdersList(con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public ArrayList<OrdersVO> selectOrdersUsers(int usersNo) {
        return null;
    }

    @Override
    public ArrayList<OrdersVO> selectOrdersDate(int ordersDate) {
        return null;
    }

    @Override
    public ArrayList<OrdersVO> selectOrdersCustomDate(int startDate, int endDate) {
        return null;
    }
}
