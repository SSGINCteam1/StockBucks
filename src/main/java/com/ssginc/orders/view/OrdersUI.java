package com.ssginc.orders.view;

import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.orders.service.DongOrdersService;
import com.ssginc.orders.service.DongOrdersServiceImpl;

import java.util.ArrayList;
import java.util.Scanner;

public class OrdersUI {
    Scanner sc;
    DongOrdersService dongOrdersService;

    public OrdersUI() {
        sc = new Scanner(System.in);
        dongOrdersService = new DongOrdersServiceImpl();
    }

    public void ordersMenu(){
        ArrayList<OrdersSelectDTO> orders = dongOrdersService.selectOrderList();

        for (OrdersSelectDTO order : orders) {
            System.out.println(order);
        }
    }
}
