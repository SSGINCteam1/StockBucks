package com.ssginc;

import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.view.LoginUI;
import com.ssginc.orders.view.OrdersUI;

public class Main {
    public static void main(String[] args) {
        LoginUI loginUI = new LoginUI();

        UsersDTO user = loginUI.loginMenu();

        if (user == null ) {
            CommonUI.displayExitMessage();
        } else if (user.getUsersRole().equals("본사")) {
            
        } else if (user.getUsersRole().equals("매니저")) {
            OrdersUI ordersUI = new OrdersUI();
            ordersUI.ordersMenu();
        } else {
            OrdersUI ordersUI = new OrdersUI();
            ordersUI.ordersMenu();
        }
    }
}
