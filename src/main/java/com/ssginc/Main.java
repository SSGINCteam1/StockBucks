package com.ssginc;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.view.LoginUI;
import com.ssginc.orders.view.OrdersUI;

public class Main {
    public static void main(String[] args) {
        LoginUI loginUI = new LoginUI();
        UsersDTO user = loginUI.loginMenu();
        OrdersUI ordersUI = new OrdersUI(user);
        ordersUI.ordersMenu();
    }
}
