package com.ssginc.placeonorders.service;

import com.ssginc.common.view.CommonUI;
import com.ssginc.placeonorders.model.dao.HoonPlaceOnOrdersDAO;
import com.ssginc.placeonorders.model.dto.HoonSelectStockListDTO;

import java.util.List;
import java.util.Scanner;

public class HoonPlaceOnOrdersUI {

    private final HoonPlaceOnOrdersDAO placeOnOrdersDAO;
    private final Scanner sc;

    public HoonPlaceOnOrdersUI() {
        this.sc = new Scanner(System.in);
        placeOnOrdersDAO = new HoonPlaceOnOrdersDAO();
    }

    public void placeOnOrderschoice() {
        boolean running = true;

        int choice = placeOnOrders(sc);

        switch (choice) {
            case 1:
                this.selectStockList();
                // 재고 조회 로직
                break;
            case 2:

                // 재고 신청 로직
                break;
            case 3:

                // 발주 내역 조회 로직
                break;
            case 4:

                // 발주 가능 품목 조회 로직
                break;
            case 5:
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
                // 재귀 호출 대신 루프에서 다시 입력을 받도록 처리
        }

    }

    public int placeOnOrders(Scanner sc) {
        System.out.println("===================================\n");
        System.out.println("[발주] \n");
        System.out.println("1. 재고 조회\t2. 재고 신청\t 3. 발주 내역 조회\t4. 발주 가능 품목 조회\t5. 종료\n");
        System.out.print(">> ");
        return sc.nextInt();
    }

    // 재고 조회 메뉴

    public void selectStockList() {
        System.out.println("===================================");
        System.out.println("[재고 조회]");
        System.out.println("1. 전체 조회\t2. 카테고리 조회\t3. 키워드 검색");
        System.out.print(">> ");
        int choice = sc.nextInt();

        List<HoonSelectStockListDTO> result = null;
        String title = null;
        switch (choice) {
            case 1:
                result = placeOnOrdersDAO.selectAllStockList();
                title = "[재고 전체 조회]";
                printStockList(result, title);
                break;
            case 2:
                System.out.println("카테고리별 재고 조회 메뉴입니다.");
                System.out.println("해당 기능은 아직 구현 중입니다.");
                break;
            case 3:
                System.out.println("키워드 검색 재고 조회 메뉴입니다.");
                System.out.println("해당 기능은 아직 구현 중입니다.");
                break;
            default:
                // 추후 CommonUI.displayWrongSelectMessage();로 변경예정
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
                this.selectStockList();
        }
    }

    public void printStockList(List<HoonSelectStockListDTO> stockList, String title) {
        System.out.println("===================================");
        System.out.println(title);
        System.out.printf("%-8s%-20s%-15s%-15s%-10s\n", "제품번호", "제품명", "재고수량", "제품 카테고리", "제품 단위");
        System.out.println("---------------------------------------------");

        String[] category = {"디저트", "MD", "일회용품", "원자재", "병음료", "원두"};
        for (HoonSelectStockListDTO stock : stockList) {
            System.out.printf("%-10s%-20s%-15s%-15s%-10s\n",
                    stock.getStNo(),
                    stock.getStName(),
                    stock.getStQuantity(),
                    category[stock.getStCategory()],
                    stock.getStUnit());
        }
    }
}
