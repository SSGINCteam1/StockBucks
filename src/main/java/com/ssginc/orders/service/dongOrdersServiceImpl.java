package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.DongOrdersDAO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;

public class dongOrdersServiceImpl implements dongOrdersService {
    DongOrdersDAO dongOrdersDAO;
    DataSource dataSource;

    public dongOrdersServiceImpl(){
        dongOrdersDAO = new DongOrdersDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }


    @Override
    public ArrayList<OrderDetailsDTO> selectOrderDetailsList() {
        ArrayList<OrderDetailsDTO> orderDetailsList = null;

        try(Connection conn = dataSource.getConnection()){
            orderDetailsList = dongOrdersDAO.selectOrderDetailsList(conn);
        } catch (Exception e){
            e.printStackTrace();
        }
        return orderDetailsList;
    }

    @Override
    public ArrayList<OrderDetailsDTO> selectOrderDetailsListByPeriod(String period, String date) {
        return null;
    }

    @Override
    public ArrayList<OrderDetailsDTO> selectOrderDetailsList(String user) {
        return null;
    }

    @Override
    public ArrayList<OrderDetailsDTO> selectOrderDetailsList(String startDate, String endDate) {
        return null;
    }
}
