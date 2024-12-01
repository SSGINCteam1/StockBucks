package com.ssginc.orders.view;

import com.ssginc.Main;
import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dto.OptionsDTO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.orders.model.dto.ProductsDTO;
import com.ssginc.orders.service.TimOrdersService;
import com.ssginc.orders.service.TimOrdersServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class OrdersUI {
    private final Scanner sc;
    private final TimOrdersService timOrdersService;

    public OrdersUI() {
        this.sc = new Scanner(System.in);
        this.timOrdersService = new TimOrdersServiceImpl();

    }

    // =================================== 주문 메뉴 실행 ===================================

    public void ordersMenu(){
        while (true) {
            System.out.println("===================================\n");
            System.out.println("[주문] \n");
            System.out.println("1. 품목 조회\t2. 품목 주문\t3. 주문 취소\t4. 주문 내역 조회\t5. 품목 판매 일시 중지");
            System.out.println("6. 상위 메뉴\t7. 종료\n");
            System.out.print(">> ");

            int choice = sc.nextInt();

            switch (choice) {
                case 4 -> this.viewOrdersHistory(); // 주문 내역 조회
                case 5 -> this.pauseSales(); // 품목 판매 일시 중지
                case 6 -> { // 상위 메뉴
                    System.out.println("상위 메뉴로 이동합니다.");
                    return;
                }
                case 7 -> { // 종료
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> CommonUI.displayWrongSelectMessage();
            }

        }
    }


    // =================================== 3. 주문 취소 ===================================

    public void displayCancelOrders(OrderDetailsDTO orderDetail){

        long diffMin = timOrdersService.getDiffMinOrderDateAndNow(orderDetail.getOrderDate());

        if (diffMin >= 5) {
            System.out.println("주문 시간 5분 경과하여 취소 불가합니다.");
        } else {
            timOrdersService.cancelOrderDetails(orderDetail);
        }

        System.out.println("1. 상위 메뉴\t2.종료\n");
        System.out.print(">> ");
        int choice = sc.nextInt();
    }

    // 1.주문 내역 선택

    // 2.주문 시간 5분 미경과?




    // =================================== 4. 주문 내역 조회 ===================================

    /**
     * 주문 내역 조회 초기 화면
     */
    public void viewOrdersHistory(){
        while (true){
            System.out.println("===================================\n");
            System.out.println("[주문 내역 조회]\n");
            System.out.println("1. 전체 조회\t2. 기간별 조회\t3. 유저별 조회\t4. 사용자 정의 조회\t5. 상위 메뉴");
            System.out.println("6. 종료\n");
            System.out.print(">> ");

            int choice = sc.nextInt();

            switch (choice){
                case 1 -> { // 전체 조회(주문 취소에서도 사용하기 위해 메뉴명과 기능 분리)
                    System.out.println("===================================\n");
                    System.out.println("[전체 주문 내역 조회]\n");
                    this.displayAllOrdersHistory();
                } 
                case 2 -> this.displayOrdersHistoryByPeriod(); // 기간별 조회
                case 3 -> this.displayOrdersHistoryByUsers(); // 유저별 조회
                case 4 ->  this.displayOrdersHistoryByCustom(); // 사용자 정의 조회
                case 5 -> {
                    System.out.println("상위 메뉴로 이동합니다.");
                    return;
                }
                case 6 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> CommonUI.displayWrongSelectMessage();
            }
        }
    }


    /**
     * 주문 목록 출력
     */
    private void printOrderList(List<OrdersSelectDTO> orders, int currentPage, int totalPages) {
        System.out.println("===================================\n");
        System.out.printf("현재 페이지: %d / %d\n", currentPage, totalPages);
        System.out.println("-----------------------------------");
        System.out.println("번호\t일시\t\t\t\t주문자\t\t결제 금액");
        System.out.println("-----------------------------------");

        int idx = 1;
        for (OrdersSelectDTO order : orders) {
            System.out.printf("%d\t%s\t%s\t%d\n", idx++, order.getOrderDate(), order.getUserName(), order.getTotalPrice());
        }
        System.out.println("-----------------------------------");
        displayPageBar(currentPage, totalPages);
    }


    /**
     * 주문 내역 목록 출력 메서드
     */
    private void displayOrdersHistory(int page, int pageSize, int totalSize, List<OrdersSelectDTO> orders) {
        // 전체 갯수
        int totalPages = (int) Math.ceil((double) totalSize / pageSize); // 총 페이지 수 계산

        while (true) {
            if (orders.isEmpty()) {
                System.out.println("\n-----------------------------------\n");
                System.out.println("주문 내역이 없습니다.\n");

                System.out.println("1. 상위 메뉴\t2.종료\n");
                System.out.print(">> ");
                int choice = sc.nextInt();

                if (choice == 1) {
                    return;
                } else if (choice == 2) {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                } else {
                    CommonUI.displayWrongSelectMessage();
                    break;
                }
            }

            this.printOrderList(orders, page, totalPages);

            int choice = selectPageMenu();

            switch (choice) {
                case 10 -> page++; // 다음 페이지
                case 11 -> page = Math.max(page - 1, 1); // 이전 페이지
                case 12 -> { // 이동할 페이지 직접 입력
                    System.out.print("이동할 페이지 번호를 입력하세요: ");
                    int newPage = sc.nextInt();
                    if (newPage > 0 && newPage <= totalPages) {
                        page = newPage;
                    } else {
                        System.out.println("유효하지 않은 페이지 번호입니다.");
                    }
                }
                case 13 -> {
                    System.out.println("상위 메뉴로 이동합니다.");
                    return;
                }
                case 14 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> {
                    if (choice >= 1 && choice <= orders.size()) { // orders.size() = 9
                        this.displayOrdersDetail(orders.get(choice - 1)); // 세부 주문 내역 조회
                    } else {
                        CommonUI.displayWrongSelectMessage();
                    }
                }
            }
        }
    }

    // -------------------------- 4.1. 전체 주문 내역 목록 출력 --------------------------
    /**
     * 전체 주문 내역 목록 출력
     */
    private void displayAllOrdersHistory(){
        int page = 1; // 현재 페이지
        int pageSize = 9; // 한 페이지에 띄울 내역 개수

        int totalSize = timOrdersService.selectOrdersListRownumAll();

        List<OrdersSelectDTO> orders = timOrdersService.selectOrderList(1, 9);

        this.displayOrdersHistory(page, pageSize, totalSize, orders);
    }


    // -------------------------- 4.2. 기간별 주문 내역 목록 출력 --------------------------
    /**
     * 기간별 주문 내역 목록 출력
     */
    private void displayOrdersHistoryByPeriod(){
        while (true){
            System.out.println("===================================\n");
            System.out.println("[기간별 주문 내역 조회]\n");

            System.out.println("1. 연도별\t2. 월별\t3. 일자별\t4. 상위 메뉴\t5. 종료\n");
            System.out.print(">> ");
            int choice = sc.nextInt();

            int year = 0;
            int month = 0;
            int day = 0;

            switch (choice){
                case 3 :
                    System.out.println("\n조회할 일을 입력해주세요(ex. 28)\n");
                    System.out.print(">> " );
                    day = sc.nextInt();
                case 2 :
                    System.out.println("\n조회할 월을 입력해주세요(ex. 2)\n");
                    System.out.print(">> " );
                    month = sc.nextInt();
                case 1 :
                    System.out.println("\n조회할 연도를 입력해주세요(ex. 2024)\n");
                    System.out.print(">> " );
                    year = sc.nextInt();
                    break;
                case 4 :
                    System.out.println("\n상위 메뉴로 이동합니다.");
                    return;
                case 5 :
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }

            int page = 1; // 현재 페이지
            int pageSize = 9; // 한 페이지에 띄울 내역 개수

            int totalSize = timOrdersService.selectOrdersListRownumByPeriod(year, month, day);

            List<OrdersSelectDTO> orders = timOrdersService.selectOrderListByPeriod(year, month, day, page, pageSize);

            this.displayOrdersHistory(page, pageSize, totalSize, orders);
        }
    }


    // -------------------------- 4.3. 유저별 주문 내역 목록 출력 --------------------------

    /**
     * 유저별 주문 내역 목록 출력
     */
    private void displayOrdersHistoryByUsers() {
        while (true){
            System.out.println("==================================\n");
            System.out.println("[유저별 주문 내역 조회]\n");
            System.out.println("1. 유저 입력\t2. 상위 메뉴\t3. 종료\n");
            System.out.print(">> ");
            int choice = sc.nextInt();

            switch (choice){
                case 1 -> this.selectUsers(false, null, null);
                case 2 -> {
                    System.out.println("\n상위 메뉴로 이동합니다.");
                    return;
                }
                case 3 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> CommonUI.displayWrongSelectMessage();
            }
        }
    }



    // -------------------------- 4.4. 사용자 정의 주문 내역 목록 출력 --------------------------

    /**
     * 사용자 정의(기간) 주문 내역 목록 출력
     */
    private void displayOrdersHistoryByCustom() {
        while(true){
            System.out.println("==================================\n");
            System.out.println("[사용자 정의 주문 내역 조회]\n");

            System.out.println("1. 조회할 기간 입력\t2. 상위 메뉴\t3. 종료\n");
            System.out.print(">> ");
            int choice = sc.nextInt();

            switch (choice){
                case 1 -> {
                    System.out.println("—-------------------------------------------------------------\n");
                    System.out.println("시작 일자(ex. 20240101)\n");
                    sc.nextLine();
                    System.out.print(">> ");
                    String startDate = sc.nextLine();
                    System.out.println("—-------------------------------------------------------------\n");
                    System.out.println("종료 일자(ex. 20240101) \n");
                    System.out.print(">> ");
                    String endDate = sc.nextLine();
                    System.out.println("—-------------------------------------------------------------\n");
                    System.out.println("1. 전체 유저 조회\t2.유저별 조회\n");
                    System.out.print(">> ");
                    int select = sc.nextInt();
                    if (select == 1 || select == 2) {
                        if (select == 1) {// 사용자 정의 방식으로 전체 유저 주문 내역 조회
                            int totalSize = timOrdersService.selectOrdersListRownumByCustom(startDate, endDate);
                            List<OrdersSelectDTO>  orders = timOrdersService.selectOrderListByCustom(startDate, endDate, 1, 9);
                            this.displayOrdersHistory(1, 9, totalSize, orders);
                        } else { // 사용자 정의 방식으로 특정 유저 주문 내역 조회
                            selectUsers(true, startDate, endDate);
                        }
                    } else {
                        CommonUI.displayWrongSelectMessage();
                    }

                }
                case 2 -> {
                    System.out.println("\n상위 메뉴로 이동합니다.");
                    return;
                }
                case 3 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> CommonUI.displayWrongSelectMessage();
            }
        }
    }


    // -------------------------- 4.5. 주문 내역 조회 공통 메서드 --------------------------

    /**
     * 유저 선택
     * @param isCustom
     * @param startDate
     * @param endDate
     */
    private void selectUsers(boolean isCustom, String startDate, String endDate) {
        int page = 1;
        int pageSize = 9;

        System.out.println("\n—-------------------------------------------------------------\n");
        System.out.println("\n조회할 유저의 이름을 입력해주세요(예: 김동현)\n");
        sc.nextLine(); // 버퍼 제거
        System.out.print(">> ");
        String userName = sc.nextLine();

        // 전체 갯수
        int totalSize = timOrdersService.selectUsersListRownumByUsersName(userName);
        int totalPages = (int) Math.ceil((double) totalSize / pageSize); // 총 페이지 수 계산

        List<UsersDTO> users = timOrdersService.selectUsersListByUsersName(userName, page, pageSize);

        while (true){
            if (users.isEmpty()) {
                System.out.println("유저 정보가 없습니다.");
                break;
            }

            System.out.println("\n—-------------------------------------------------------------\n");
            System.out.println("조회할 유저를 선택해주세요\n");

            for (int i = 0; i < users.size(); i++) {
                UsersDTO user = users.get(i);
                System.out.println((i + 1) + ". " + user.getUsersName() + "(" + user.getUsersBirth() + ")");
            }

            System.out.println();

            displayPageBar(page, pageSize);

            int choice = selectPageMenu();

            switch (choice) {
                case 10 -> page++; // 다음 페이지
                case 11 -> page = Math.max(page - 1, 1); // 이전 페이지
                case 12 -> { // 이동할 페이지 직접 입력
                    System.out.print("이동할 페이지 번호를 입력하세요: ");
                    int newPage = sc.nextInt();
                    if (newPage > 0 && newPage <= totalPages) {
                        page = newPage;
                    } else {
                        System.out.println("유효하지 않은 페이지 번호입니다.");
                    }
                }
                case 13 -> {
                    System.out.println("상위 메뉴로 이동합니다.");
                    return;
                }
                case 14 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> {
                    if (choice >= 1 && choice <= users.size()) { // users.size() = 9
                        int usersNo = users.get(choice - 1).getUsersNo();

                        int totalSizeForOrders = 0;
                        List<OrdersSelectDTO> orders = new ArrayList<>();

                        if (!isCustom){ // 사용자 정의 방식 아닐 시
                            totalSizeForOrders = timOrdersService.selectOrdersListRownumByUsers(usersNo);
                            orders = timOrdersService.selectOrdersListByUsersNo(usersNo, page, pageSize);
                        } else { // 사용자 정의 방식일 시
                            totalSizeForOrders = timOrdersService.selectOrdersListRownumByCustomAndUsersNo(usersNo, startDate, endDate);
                            orders = timOrdersService.selectOrderListByCustomAndUsersNo(usersNo, startDate, endDate, 1, 9);
                        }
                        this.displayOrdersHistory(1, 9, totalSizeForOrders, orders); // 세부 주문 내역 조회

                    } else {
                        CommonUI.displayWrongSelectMessage();
                    }
                }
            }
        }
    }

    /**
     * 주문 세부 내역 조회
     * @param order
     */
    public void displayOrdersDetail(OrdersSelectDTO order){
        StringBuffer sb = new StringBuffer();

        OrderDetailsDTO orderDetail = timOrdersService.selectOrdersDetail(order.getOrderNo());

        sb.append("\n").append("===================================").append("\n");
        sb.append("\n").append("[").append(order.getOrderDate()).append("]").append(" 주문 내역을 불러왔습니다.").append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append(order.getOrderDate()).append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append("주문번호 : ").append(order.getOrderNo()).append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append("주문자 : ").append(order.getUserName()).append("\n");
        sb.append("----------------------------------------").append("\n");
        for (ProductsDTO products : orderDetail.getProducts()){
            sb.append(products.getPname()).append("\t").append(products.getPrice()).append("\t").append(products.getQuantity()).append("\n");
            for (OptionsDTO options : products.getOptions()){
                sb.append(options.getOptName()).append("\t").append(options.getPrice()).append("\t").append(options.getQuantity()).append("\n");
            }
        }
        sb.append("----------------------------------------").append("\n");
        sb.append("합계").append("\t\t\t").append(order.getTotalPrice()).append("\n");
        sb.append("----------------------------------------").append("\n");

        System.out.println(sb);

        System.out.println("1. 주문 취소\t2.상위 메뉴\t3.종료");
        sc.nextLine();
        System.out.print(">> ");
        int select = sc.nextInt();

        if (select == 1) {
            this.displayCancelOrders(orderDetail);
        }
        else if (select == 2){
            return;
        } else if (select == 3){
            CommonUI.displayExitMessage();
            System.exit(0);
        } else {
            CommonUI.displayWrongSelectMessage();
        }

    }

    /**
     * 전체 주문 내역 목록 페이지 처리 메서드
     * @param currentPage
     * @param totalPages
     */
    private void displayPageBar(int currentPage, int totalPages) {
        StringBuffer sb = new StringBuffer();

        sb.append("\t\t\t");

        // 이전 페이지 표시
        if (currentPage > 1) {
            sb.append("[이전] ");
        }

        // 현재 페이지를 중심으로 페이지바 표시 (최대 5개 페이지)
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        for (int i = startPage; i <= endPage; i++) {
            if (i == currentPage) {
                sb.append("[").append(i).append("] "); // 현재 페이지 강조
            } else {
                sb.append(i).append(" ");
            }
        }

        // 다음 페이지 표시
        if (currentPage < totalPages) {
            sb.append("[다음]");
        }

        sb.append("\t\t\t");

        System.out.println(sb);
    }

    /**
     * 주문 내역 목록 조회 선택 메뉴 출력
     * @return
     */
    public int selectPageMenu(){
        System.out.println("\n10. 다음 페이지\t11. 이전 페이지\t12. 페이지 입력\t13. 상위 메뉴\t14. 종료\n");
        System.out.print(">> ");
        return sc.nextInt();
    }


    // =================================== 5. 품목 판매 중지 ===================================
    private void displayPauseSales() {

    }

}
