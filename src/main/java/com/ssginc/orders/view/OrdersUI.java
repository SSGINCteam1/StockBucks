package com.ssginc.orders.view;

import com.ssginc.Main;
import com.ssginc.common.view.CommonUI;
import com.ssginc.orders.model.dto.OptionsDTO;
import com.ssginc.orders.model.dto.OrderDetailsDTO;
import com.ssginc.orders.model.dto.OrdersSelectDTO;
import com.ssginc.orders.model.dto.ProductsDTO;
import com.ssginc.orders.service.TimOrdersService;
import com.ssginc.orders.service.TimOrdersServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

@Slf4j
public class OrdersUI {
    Scanner sc;
    TimOrdersService timOrdersService;
    public OrdersUI() {
        sc = new Scanner(System.in);
        timOrdersService = new TimOrdersServiceImpl();
    }

    public void ordersMenu(){
        while (true) {
            System.out.println("===================================\n");
            System.out.println("[주문] \n");

            int choice = ordersSelectMenu(sc);

            switch (choice) {
                case 1: // 품목 조회
                    break;
                case 2: // 품목 주문
                    break;
                case 3: // 주문 취소
                    break;
                case 4: // 주문 내역 조회
                    viewOrdersHistory();
                    break;
                case 5: // 품목 판매 일시 중지
                    break;
                case 6: // 상위 메뉴
                    System.out.println("상위 메뉴로 이동합니다.");
                    return; // 최상위 메뉴로 돌아가기 위해 return
                case 7: // 종료
                    CommonUI.displayExitMessage();
                    System.exit(0);
                    break;
                default:
                    CommonUI.displayWrongSelectMessage();
            }
        }
    }


    // 주문 시작 화면
    public int ordersSelectMenu(Scanner sc){
        System.out.println("1. 품목 조회\t2. 품목 주문\t3. 주문 취소\t4. 주문 내역 조회\t5. 품목 판매 일시 중지\n" +
                "6. 상위 메뉴\t7. 종료\n");
        System.out.print(">> ");
        return sc.nextInt();
    }


    // =================================== 1. 품목 조회 ===================================

    // =================================== 2. 품목 주문 ===================================

    // =================================== 3. 주문 취소 ===================================

    // =================================== 4. 주문 내역 조회 ===================================

    /**
     * 주문 내역 조회 초기 화면
     */
    public void viewOrdersHistory(){
        System.out.println("===================================\n");
        System.out.println("[주문 내역 조회]\n");

        int choice = viewOrdersHistorySelectMenu(sc);

        switch (choice){
            case 1: // 전체 조회
                displayAllOrdersHistory();
                break;
            case 2: // 기간별 조회
                break;
            case 3: // 유저별 조회
                break;
            case 4: // 사용자 정의 조회
                break;
            case 5: // 상위 메뉴
                break;
            case 6: // 종료
                break;
            default:
                CommonUI.displayWrongSelectMessage();
                this.viewOrdersHistory();
        }

    }

    /**
     * 주문 내역 조회 초기 화면 선택 메뉴 출력
     * @param sc
     * @return
     */
    public int viewOrdersHistorySelectMenu(Scanner sc){
        System.out.println("1. 전체 조회\t2. 기간별 조회\t3. 유저별 조회\t4. 사용자 정의 조회\t5. 상위 메뉴\n" +
                "6. 종료\n");
        System.out.print(">> ");
        return sc.nextInt();
    }

    /**
     * 전체 주문 내역 목록 출력
     */
    public void displayAllOrdersHistory(){
        System.out.println("===================================\n");
        System.out.println("[전체 주문 내역 조회]\n");

        int page = 1; // 초기 페이지
        int pageSize = 9; // 한 페이지에 표시할 항목 수

        // 전체 갯수
        int totalSize = timOrdersService.selectOrdersAllRownum();
        int pageCount = (int) Math.ceil((double) totalSize / pageSize); // 총 페이지 수 계산

        while (true) {
            int idx = 1;
            List<OrdersSelectDTO> orders = timOrdersService.selectOrderList(page, pageSize);

            if (orders.isEmpty()){
                System.out.println("더 이상 주문 내역이 없습니다.");
                break;
            }

            StringBuffer sb = new StringBuffer();
            sb.append("\n");
            sb.append("—-------------------------------------------------------------\n");
            sb.append("\t\t\t일시\t\t\t| 주문자 | 결제 금액").append("\n");
            sb.append("—-------------------------------------------------------------\n");

            for (OrdersSelectDTO order : orders) {
                sb.append(idx++).append(".").append(" ").append(order.getOrderDate())
                        .append(" ").append("|").append(" ").append(order.getUserName())
                        .append(" ").append("|").append(" ").append(order.getTotalPrice());
                sb.append("\n");
            }

            System.out.println(sb);

            // 페이지바 출력
            displayPageBar(page, pageCount);

            int choice = displayAllOrdersHistorySelectMenu(sc);

            switch (choice){
                case 1,2,3,4,5,6,7,8,9 : // 상세 주문 내역 조회
                    OrdersSelectDTO order = orders.get(choice - 1);

                    int orderNo = order.getOrderNo();
                    String orderDate = order.getOrderDate();
                    String userName = order.getUserName();
                    int totalPrice = order.getTotalPrice();

                    displayOrdersDetail(orderNo, orderDate, userName, totalPrice);
                    break;
                case 10: // 다음 페이지
                    page++;
                    break;
                case 11: // 이전 페이지
                    if (page > 1) {
                        page--;
                    } else {
                        System.out.println("이전 페이지로 갈 수 없습니다.");
                    }
                    break;
                case 12: // 페이지 입력
                    System.out.print("이동할 페이지 번호를 입력하세요: ");
                    int newPage = sc.nextInt();
                    if (newPage > 0) {
                        page = newPage;
                    } else {
                        System.out.println("유효하지 않은 페이지 번호입니다.");
                    }
                    break;
                case 13: // 상위 메뉴
                    return;
                case 14: // 종료
                    CommonUI.displayExitMessage();
                    System.exit(0);
                    break;
                default:
                    CommonUI.displayWrongSelectMessage();
            }
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
     * @param sc
     * @return
     */
    public int displayAllOrdersHistorySelectMenu(Scanner sc){
        System.out.println("\n10. 다음 페이지\t11. 이전 페이지\t12. 페이지 입력\t13. 상위 메뉴\t14. 종료\n");
        System.out.print(">> ");
        return sc.nextInt();
    }

    private void displayOrdersDetail(int orderNo, String orderDate, String userName, int totalPrice) {
        StringBuffer sb = new StringBuffer();

        OrderDetailsDTO orderDetail = timOrdersService.selectOrdersDetail(orderNo);

        int price = 0;

        sb.append("\n").append("===================================").append("\n");
        sb.append("[").append(orderDate).append("]").append(" 주문 내역을 불러왔습니다.").append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append(orderDate).append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append("주문번호 : ").append(orderNo).append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append("주문자 : ").append(userName).append("\n");
        sb.append("----------------------------------------").append("\n");
        for (ProductsDTO products : orderDetail.getProducts()){
            sb.append(products.getPname()).append("\t").append(products.getPrice()).append("\t").append(products.getQuantity()).append("\n");
            price += products.getPrice() * products.getQuantity();
            for (OptionsDTO options : products.getOptions()){
                sb.append(options.getOptName()).append("\t").append(options.getPrice()).append("\t").append(options.getQuantity()).append("\n");
                price += options.getPrice() * options.getQuantity();
            }
        }
        sb.append("----------------------------------------").append("\n");
        sb.append("합계").append("\t\t\t").append(price).append("\n");
        sb.append("----------------------------------------").append("\n");

        System.out.println(sb);
    }

    // 5. 품목 판매 일시 중지


    // 6. 상위 메뉴
}
