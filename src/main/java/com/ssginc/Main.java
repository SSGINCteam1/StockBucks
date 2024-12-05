package com.ssginc;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.view.LoginUI;
import com.ssginc.manageproduct.view.ManageProductUI;
import com.ssginc.orders.view.OrdersUI;
import com.ssginc.placeonorders.view.PlaceOnOrdersUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        LoginUI login = new LoginUI();
        UsersDTO user = login.loginMenu();

        int userRole = user.getUsersRole();

        while (true) {
            switch (userRole) {
                // 본사
                case 0:
                    ManageProductUI manageProductUI = new ManageProductUI(sc);
                    manageProductUI.selectManageProductMenu();
                    break;
                // 매니저
                case 1:
                    System.out.println("1. 발주\t2. 주문");
                    System.out.println("메뉴를 선택하여주세요.");
                    System.out.print(">> ");
                    int choice = sc.nextInt();

                    if (choice == 1) {
                        PlaceOnOrdersUI placeOnOrdersUI = new PlaceOnOrdersUI(user, sc);
                        placeOnOrdersUI.placeOnOrderschoice();
                    } else {
                        OrdersUI ordersUI = new OrdersUI(user);
                        ordersUI.ordersMenu();
                    }
                    break;
                // 사원
                case 2:
                    OrdersUI ordersUI = new OrdersUI(user);
                    ordersUI.ordersMenu();
                    break;
            }
        }
    }
}
