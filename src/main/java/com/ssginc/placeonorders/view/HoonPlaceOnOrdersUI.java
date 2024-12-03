package com.ssginc.placeonorders.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.placeonorders.model.dao.HoonPlaceOnOrdersDAO;
import com.ssginc.placeonorders.model.dto.HoonSelectBasketListDTO;
import com.ssginc.placeonorders.model.dto.HoonSelectStockListDTO;

import java.util.List;
import java.util.Scanner;

public class HoonPlaceOnOrdersUI {

    private final Scanner sc;
    private final HoonPlaceOnOrdersDAO placeOnOrdersDAO;
    private final UsersDTO user;
    String[] category = {"디저트", "MD", "일회용품", "원자재", "병음료", "원두"};

    public HoonPlaceOnOrdersUI(UsersDTO user) {
        this.sc = new Scanner(System.in);
        placeOnOrdersDAO = new HoonPlaceOnOrdersDAO();
        this.user = user;
    }

    // 발주 메뉴 선택 메서드
    public void placeOnOrderschoice() {
        boolean running = true;

        int choice = placeOnOrders(sc);

        switch (choice) {
            case 1:
                this.selectStockList();     // 재고 조회 로직
                break;
            case 2:
                this.registerPlaceOnOrdersMenu();   // 발주 신청 로직
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

    // 발주 메뉴 출력 메서드
    public int placeOnOrders(Scanner sc) {
        System.out.println("===================================\n");
        System.out.println("[발주] \n");
        System.out.println("1. 재고 조회\t2. 발주 신청\t3. 발주 내역 조회\t4. 발주 가능 품목 조회\t5. 종료\n");
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
            // 전체 조회
            case 1 -> {
                result = placeOnOrdersDAO.selectAllStockList();

                if (result == null) {
                    // CommonUI의 메서드로 빼는 것도 좋을 거 같다고 생각
                    System.out.println("조회된 결과가 없습니다.");
                    break;
                }

                title = "[재고 전체 조회]";
                this.printStockList(result, title);
            }

            // 카테고리 조회
            case 2 -> {
                int categoryNum = selectCategory();
                result = placeOnOrdersDAO.selectStockListByCategory(categoryNum);

                if (result == null) {
                    // CommonUI의 메서드로 빼는 것도 좋을 거 같다고 생각
                    System.out.println("조회된 결과가 없습니다.");
                    break;
                }

                title = "[재고 카테고리별 조회]";
                this.printStockList(result, title);
            }

            // 키워드 검색
            case 3 -> {
                String searchKeyword = inputKeyword();
                result = placeOnOrdersDAO.selectStockListByKeyword(searchKeyword);

                if (result == null) {
                    // CommonUI의 메서드로 빼는 것도 좋을 거 같다고 생각
                    System.out.println("조회된 결과가 없습니다.");
                    break;
                }

                title = "[재고 키워드 검색]";
                this.printStockList(result, title);
            }

            default -> {
                // 추후 CommonUI.displayWrongSelectMessage();로 변경예정
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
                this.selectStockList();
            }
        }
    }

    // 발주 신청 메뉴
    public void registerPlaceOnOrdersMenu() {
        System.out.println("===================================");
        System.out.println("[발주 신청]");
        System.out.println();

        if (this.user == null) {
            System.out.println("회원 정보가 잘못되었습니다.");
            return;
        }
        List<HoonSelectBasketListDTO> basketList = placeOnOrdersDAO.selectBasketListByUsersNo(this.user.getUsersNo());
        this.printBasketList(basketList);

        System.out.println("===================================");
        System.out.println("1. 품목 선택\t2. 발주 신청");
        System.out.print(">> ");
//        int choice = sc.nextInt();
    }

    // 재고 리스트 출력 메서드
    public void printStockList(List<HoonSelectStockListDTO> stockList, String title) {
        System.out.println("===================================");
        System.out.println(title);
        System.out.printf("%-8s%-20s%-15s%-15s%-10s\n", "제품번호", "제품명", "재고수량", "제품 카테고리", "제품 단위");
        System.out.println("---------------------------------------------");

        for (HoonSelectStockListDTO stock : stockList) {
            System.out.printf("%-10s%-20s%-15s%-15s%-10s\n",
                    stock.getStNo(),
                    stock.getStName(),
                    stock.getStQuantity(),
                    category[stock.getStCategory()],
                    stock.getStUnit());
        }
    }

    // 장바구니 리스트 출력 메서드
    public void printBasketList(List<HoonSelectBasketListDTO> basketList) {
        System.out.printf("%-10s%-20s%-10s%-10s%-10s%-20s%-10s\n",
                "제품번호", "제품명", "단가", "발주수량", "발주가격", "제품 카테고리", "제품 단위");
        System.out.println("---------------------------------------------");

        int totalPrice = 0;
        for (HoonSelectBasketListDTO basketStock : basketList) {
            int subTotal = basketStock.getPlaceOrdersPrice();

            System.out.printf("%-10s%-20s%-10d%-10d%-10d%-20s%-10s\n",
                    basketStock.getStNo(),
                    basketStock.getStName(),
                    basketStock.getStPrice(),
                    basketStock.getPlaceOrdersQuantity(),
                    basketStock.getPlaceOrdersPrice(),
                    category[basketStock.getStCategory()],
                    basketStock.getStUnit());

            totalPrice += subTotal;
        }

        System.out.println("---------------------------------------------");
        System.out.println("Total | \t" + totalPrice);
    }

    // 카테고리 입력 메서드
    public int selectCategory() {
        System.out.println("===================================");
        for (int i = 0; i < category.length; i++) {
            System.out.print((i + 1) + ". " + category[i] + "\t\t");
        }
        System.out.println();
        System.out.print(">> ");

        return sc.nextInt();
    }

    // 검색 키워드 입력 메서드
    public String inputKeyword() {
        sc.nextLine();  // 입력 버퍼 비워주기
        System.out.println("===================================");
        System.out.println("검색할 단어를 입력하세요.");
        System.out.print(">> ");

        return sc.nextLine();
    }
}
