package com.ssginc.orders.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.orders.service.DongOrdersService;
import com.ssginc.orders.service.DongOrdersServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrdersUI {
    Scanner sc;
    DongOrdersService dongOrdersService;

    public OrdersUI() {
        sc = new Scanner(System.in);
        dongOrdersService = new DongOrdersServiceImpl();
    }

    public void ordersMenu(){
        int choice = ordersSelectMenu(sc);

        switch (choice){
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
                ordersMenu();
        }

    }


    // 주문 시작 화면
    public int ordersSelectMenu(Scanner sc){
        System.out.println("===================================\n");
        System.out.println("[주문] \n");
        System.out.println("1. 품목 조회\t2. 품목 주문\t3. 주문 취소\t4. 주문 내역 조회\t5. 품목 판매 일시 중지\n" +
                "6. 상위 메뉴\t7. 종료\n");
        sc.nextLine(); // 버퍼 제거
        System.out.println(">> ");
        return sc.nextInt();
    }


    // 1. 품목 조회

    // 2. 품목 주문

    // 3. 주문 취소

    // 4. 주문 내역 조회

    // 5. 품목 판매 일시 중지

    // 6. 상위 메뉴
}
