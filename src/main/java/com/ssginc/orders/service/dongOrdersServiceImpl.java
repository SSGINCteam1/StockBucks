package com.ssginc.orders.service;

import com.ssginc.orders.model.dao.DongOrdersDAO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;

import java.util.ArrayList;

public class dongOrdersServiceImpl implements dongOrdersService {
    DongOrdersDAO dongOrdersDAO;
//    DataS


    @Override
    public ArrayList<OrderDetailsDTO> selectOrderDetailsList() {

        return null;
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
