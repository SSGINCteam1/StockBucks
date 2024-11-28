package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.DongOrdersDAO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;

public class DongOrdersServiceImpl implements DongOrdersService {
    DongOrdersDAO dongOrdersDAO;
    DataSource dataSource;

    public DongOrdersServiceImpl(){
        dongOrdersDAO = new DongOrdersDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }


    @Override
    public ArrayList<OrdersSelectDTO> selectOrderList() {
        ArrayList<OrdersSelectDTO> orders = null;

        try(Connection conn = dataSource.getConnection()){
            orders = dongOrdersDAO.selectOrderDetailsList(conn);
        } catch (Exception e){
            e.printStackTrace();
        }
        return orders;
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
