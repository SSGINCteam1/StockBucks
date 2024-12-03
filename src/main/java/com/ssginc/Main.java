package com.ssginc;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.view.LoginUI;
import com.ssginc.orders.view.OrdersUI;
import com.ssginc.orders.view.WishOrdersUI;

public class Main {
    public static void main(String[] args) {
//        LoginUI loginUI = new LoginUI();
//        UsersDTO user = loginUI.loginMenu();
//        WishOrdersUI wishOrdersUI = new WishOrdersUI(user);
//        wishOrdersUI.orderMenu();

        OrdersUI ordersUI = new OrdersUI();
        ordersUI.ordersMenu();


    }
}
