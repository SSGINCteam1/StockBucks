package com.ssginc.orders.view;

import com.ssginc.Main;
import com.ssginc.common.view.ANSIStyle;
import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.orders.model.dto.*;
import com.ssginc.orders.service.TimOrdersService;
import com.ssginc.orders.service.TimOrdersServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class OrdersUI {
    private final Scanner sc;
    private final TimOrdersService timOrdersService;

    private UsersDTO user;
    private static List<ProductsDTO> order;

    public OrdersUI(UsersDTO user) {
        this.sc = new Scanner(System.in);
        this.timOrdersService = new TimOrdersServiceImpl();
        this.user = user;
    }

    // =================================== 주문 메뉴 실행 ===================================

    public void ordersMenu() {
        String[] menus = {"1. 품목 조회", "2. 품목 주문", "3. 주문 내역 조회", "4. 주문 취소", "5. 품목 판매 중지", "6. 상위 메뉴", "7. 시스템 종료"};

        while (true) {
            System.out.println("==================================================================");
            CommonUI.printCentered("[주문 메뉴]");
            System.out.println("==================================================================\n");

            System.out.printf(
                    "%-20s %-20s %-20s\n",
                    menus[0], menus[1], menus[2]
            );
            System.out.printf(
                    "%-20s %-20s %-20s\n",
                    menus[3], menus[4], menus[5]
            );
            System.out.printf(
                    "%-20s\n",
                    ANSIStyle.RED + menus[6] + ANSIStyle.RESET
            );

            // 하단 구분선 및 입력 대기
            System.out.println("\n==================================================================");

            int choice = safeInput();
            
            switch (choice) {
                case 1, 2, 5 ->{
                    int purpose = 0;
                    if (choice == 1) {
                        purpose = 1; // 품목 조회
                        System.out.println("\n>> [품목 조회 메뉴로 이동합니다.]");
                    } else if (choice == 2){
                        purpose = 2; // 품목 주문
                        System.out.println("\n>> [품목 주문 메뉴로 이동합니다.]");
                    } else {
                        purpose = 3; // 품목 판매 중지
                        System.out.println("\n>> [품목 판매 중지 메뉴로 이동합니다.]");
                    }
                    this.displayPrdAndSt(purpose);
                } // 품목 조회
                case 3 -> {
                    System.out.println("\n>> [주문 내역 조회 메뉴로 이동합니다.]");
                    this.viewOrdersHistory();
                } // 주문 내역 조회
                case 4 -> {
                    System.out.println("\n>> [주문 취소 메뉴로 이동합니다.]");
                    this.displayAllOrdersHistory(2);
                } // 주문 취소
                case 6 -> { // 상위 메뉴
                    CommonUI.displayGoBackMessage();
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

    // =================================== 1. 품목 조회 ===================================

    /**
     * 품목 조회 화면
     * @param purpose   사용 목적 (1: 품목 조회, 2: 품목 주문, 3: 품목 판매 중지)
     */
    private void displayPrdAndSt(int purpose) {
        String[] bigCategory = {"[품목 조회]", "[품목 주문]", "[품목 판매 중지]"};
        String[] menus = {"1. 음료", "2. 푸드", "3. 상품", "4. 상위 메뉴", "5. 종료"};

        while (true) {
            System.out.println("==================================================================");
            CommonUI.printCentered(bigCategory[purpose - 1]);
            System.out.println("==================================================================\n");

            System.out.printf(
                    "%-20s %-20s %-20s\n",
                    menus[0], menus[1], menus[2]
            );
            System.out.printf(
                    "%-20s %-20s\n",
                    menus[3], menus[4]
            );

            System.out.println("\n==================================================================");

            int choice = safeInput();

            switch (choice) {
                case 1 -> this.selectBeverageCategory(purpose);
                case 2 -> this.selectFoodOrMdMenu(purpose, true);
                case 3 -> this.selectFoodOrMdMenu(purpose, false);
                case 4 -> {
                    CommonUI.displayGoBackMessage();
                    return;
                }
                case 5 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> CommonUI.displayWrongSelectMessage();
            }
        }
    }


    // -------------------------- 1.1. 음료 품목 조회 --------------------------


    /**
     * 음료 카테고리 조회
     * @param purpose   사용 목적 (1: 품목 조회, 2: 품목 주문, 3: 품목 판매 중지)
     */
    private void selectBeverageCategory(int purpose) {

        int currpage = 1;

        int pageSize = 9;

        int totalSize = timOrdersService.selectPrdCgListRownumAll();

        List<PrdCgDTO> prdCgs = timOrdersService.selectPrdCgListAll(currpage, pageSize);

        String[] menus = {"[음료 조회]", "[음료 주문]", "[음료 판매 중지]"};


        while (true) {
            System.out.println("==================================================================");
            CommonUI.printCentered(menus[purpose - 1]);
            System.out.println("==================================================================\n");

            if (prdCgs == null || prdCgs.isEmpty()) {
                displayEmptyMsg();
                return;
            }

            int idx = 0;

            for (idx = 0; idx < prdCgs.size(); idx++) {
                System.out.printf("%-20s", ((idx+ 1) + ". " + prdCgs.get(idx).getPrdCgName()));

                if ((idx + 1) % 3 == 0) {
                    System.out.println();
                }
            }

            String[] str = {"병음료", "상위 메뉴", ANSIStyle.RED + "시스템 종료" + ANSIStyle.RESET};

            for (int i = 0; i < str.length; i++) {
                System.out.printf("%-20s", ((idx + 1) + ". " + str[i]));

                if ((idx++ + 1) % 3 == 0) {
                    System.out.println();
                }
            }

            System.out.println("\n==================================================================");
            int choice = safeInput();

            if (choice >= 1 && choice <= prdCgs.size()) { // 음료
                selectBeverageMenu(purpose, prdCgs.get(choice - 1).getPrdCgName(), choice);
                if (purpose == 1) return; // 조회용이면 옵션 볼 필요 없기에 탈출
            } else if (choice == prdCgs.size() + 1) { // 병음료
                selectBottleMenu(purpose, "병음료");
                if (purpose == 1) return;
            } else if (choice == prdCgs.size() + 2) {
                CommonUI.displayGoBackMessage();
                return;
            } else if (choice == prdCgs.size() + 3) {
                CommonUI.displayExitMessage();
                System.exit(0);
            } else {
                CommonUI.displayWrongSelectMessage();
            }


            System.out.println("\n==================================================================\n");
        }
    }

    private void selectBeverageMenu(int purpose, String prdcgName, int prdcgNo) {
        String[] menus = { "[" + prdcgName + " 조회]", "[" + prdcgName + " 주문]", "[" + prdcgName + " 판매 일시 중지]"};

        int currPage = 1;
        int pageSize = 9;

        while (true) {
            ArrayList<ProductsDTO> products = timOrdersService.selectProductsListByPrdcgNo(prdcgNo, purpose, currPage, pageSize);
            int totalPages = timOrdersService.selectProductsListRownumByPrdcgNo(prdcgNo, purpose);

            System.out.println("==================================================================");
            CommonUI.printCentered(menus[purpose - 1]);
            System.out.println("==================================================================\n");

            if (products == null || products.isEmpty()) {
                displayEmptyMsg();
                return;
            }

            int idx = 0;

            System.out.printf("현재 페이지: %d / %d\n", currPage, totalPages);
            System.out.println("------------------------------------------------------------------");
            System.out.printf(
                    "%-10s %-30s %-10s %-10s\n",
                    "번호", "품목명", "가격", "상태"
            );
            System.out.println("------------------------------------------------------------------");

            for (idx = 0; idx < products.size(); idx++) {
                ProductsDTO product = products.get(idx);
                System.out.printf(
                        "%-10d %-30s %-10d %-10s\n",
                        idx + 1, product.getPname(), product.getPrice(), product.isActive() ? "판매중" : "판매 중지"
                );
            }
            System.out.println("------------------------------------------------------------------");

            CommonUI.displayPageBar(currPage, totalPages);

            int choice = selectPageMenu();

            switch (choice) {
                case 10: // 다음 페이지
                    if (currPage < totalPages) currPage++;
                    break;
                case 11: // 이전 페이지
                    if (currPage > 1) currPage--;
                    break;
                case 12: // 페이지 직접 입력
                    System.out.print("이동할 페이지 번호를 입력하세요: ");
                    int newPage = sc.nextInt();
                    if (newPage > 0 && newPage <= totalPages) {
                        currPage = newPage;
                    } else {
                        System.out.println("유효하지 않은 페이지 번호입니다.");
                    }
                    break;
                case 13: // 상위 메뉴
                    CommonUI.displayGoBackMessage();
                    return;
                case 14: // 종료
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    if (purpose == 2) { // 품목 주문
                        if (choice >= 1 && choice <= 9) {
                            selectBeverageOption(products.get(choice - 1));
                            return;
                        }
                    } else if (purpose == 3){ // 판매 중지
                        if (choice >= 1 && choice <= 9) {
                            this.displayPauseSale(products.get(choice - 1));
                            currPage = 1;
                            break;
                        }
                    } else {
                        CommonUI.displayWrongSelectMessage();
                    }
            }


            System.out.println("\n==================================================================\n");
        }
    }


    // -------------------------- 1.2. 푸드/MD 품목 조회 --------------------------

    /**
     * 푸드 / MD 조회
     */
    private void selectFoodOrMdMenu(int purpose, boolean isFood) {

        int type1 = isFood ? 0 : 1;

        int currentPage = 1;

        int pageSize = 9;

        int totalSize = timOrdersService.selectEtcListRownumAll(type1, false);

        ArrayList<ProductsDTO> products = timOrdersService.selectEtcListAll(purpose, type1, currentPage, pageSize);

        String type = isFood ? "푸드" : "MD";


        String[] menus = {"[" + type + "조회]\n", "[" + type + "주문]\n", "[" + type + "판매 일시 중지]\n"};

        while (true) {
            System.out.println("==================================================================\n");
            System.out.println(menus[purpose - 1]);

            if (products == null || products.isEmpty()) {
                displayEmptyMsg();
                return;
            }

            int preMenu = products.size() + 1;
            int exit = products.size() + 2;

            for (int i = 0; i < products.size(); i++) {

                ProductsDTO product = products.get(i);

                System.out.print((i + 1) + ". " + product.getPname() + " - " + product.getPrice() + "원");
                if (!product.isActive()){
                    System.out.println(" (판매 중지)");
                }
            }
            System.out.print(preMenu + ". \t" + "상위 메뉴");
            System.out.print("\t" + exit + ". \t" + "종료");
            int choice = safeInput();

            if (purpose == 2) { // 품목 주문
                if (choice >= 1 && choice < preMenu) {
                    selectBeverageOption(products.get(choice - 1));
                    return;
                }
            } else if (purpose == 3){ // 판매 중지
                if (choice >= 1 && choice < preMenu) {
                    this.displayPauseSale(products.get(choice - 1));
                    return;
                }
            }

            if (choice == preMenu) {
                return;
            } else if (choice == exit) {
                CommonUI.displayExitMessage();
                System.exit(0);
            } else {
                CommonUI.displayWrongSelectMessage();
            }

            System.out.println("\n==================================================================\n");
        }
    }

    private void selectBottleMenu(int purpose, String prdcgName) {

        int currPage = 1;

        int pageSize = 9;

        int totalSize = timOrdersService.selectEtcListRownumAll(4, false);


        ArrayList<ProductsDTO> products = timOrdersService.selectEtcListAll(purpose, 4, currPage, pageSize);

        String[] menus = {"조회]\n", "주문]\n", "판매 일시 중지]\n"};

        while (true) {
            System.out.println("==================================================================\n");
            System.out.println("[" + prdcgName + " " + menus[purpose - 1]);

            if (products == null) {
                displayEmptyMsg();
                return;
            }

            int preMenu = products.size() + 1;
            int exit = products.size() + 2;

            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i).getPname() + " - " + products.get(i).getPrice() + "원");
            }
            System.out.print(preMenu + ". \t" + "상위 메뉴");
            System.out.print("\t" + exit + ". \t" + "종료");
            int choice = safeInput();

            System.out.println("\n==================================================================\n");


            if (purpose == 2) { // 품목 주문
                if (choice >= 1 && choice < preMenu) {
                    ProductsDTO product = products.get(choice - 1);
                    product.setIsBeverage(2);
                    addtionalOrder(product);
                    return;
                }
            } else if (purpose == 3){ // 판매 중지
                if (choice >= 1 && choice < preMenu) {
                    this.displayPauseSale(products.get(choice - 1));
                    return;
                }
            }

            if (choice == preMenu) {
                return;
            } else if (choice == exit) {
                CommonUI.displayExitMessage();
                System.exit(0);
            } else {
                CommonUI.displayWrongSelectMessage();
            }

            System.out.println("\n==================================================================\n");
        }
    }

    // =================================== 2. 품목 주문 ===================================

    private void selectBeverageOption(ProductsDTO product) {
        product.setIsBeverage(1);

        List<OptionsDTO> opts = new ArrayList<>();

        ArrayList<PrdOptDTO> prdopt = timOrdersService.selectPrdOpt(product.getPno());

        while (true) {
            System.out.println("===================================\n");
            System.out.println("[주문 옵션 선택]\n");

            if (prdopt == null) {
                displayEmptyMsg();
                return;
            }

            System.out.println("\n1. 옵션 선택\t2.다른 상품 담기\t3.종료\n");
            int choice = safeInput();

            if (choice != 1) {
                if (choice == 2) {
                    CommonUI.displayGoBackMessage();
                    return;
                } else if (choice == 3) {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                } else {
                    CommonUI.displayWrongSelectMessage();
                    continue;
                }
            }

            for (int i = 0; i < prdopt.size(); i++) {
                System.out.println((i + 1) + ". "
                        + prdopt.get(i).getOptCategoryName());

                List<PrdOptDetailDTO> details = prdopt.get(i).getOptionDetails();

                for (int j = 0; j < details.size(); j++) {
                    System.out.print("\t" + (j + 1) + ". "
                            + details.get(j).getOptionName());
                }


                choice = safeInput();

                PrdOptDetailDTO opt = null;

                if (choice >= 1 && details.size() == 1) {
                    opt = details.get(0);
                    opts.add(OptionsDTO.builder()
                            .optNo(opt.getOptNo())
                            .optName(opt.getOptionName())
                            .quantity(choice)
                            .price(opt.getOptionPrice())
                            .build());
                } else if (choice >= 1 && choice <= details.size()) {
                    opt = details.get(choice - 1);
                    opts.add(OptionsDTO.builder()
                            .optNo(opt.getOptNo())
                            .optName(opt.getOptionName())
                            .quantity(0)
                            .price(opt.getOptionPrice())
                            .build());
                }

                System.out.println();
            }

            System.out.println("\n=======================================\n");
            System.out.println("<주문 확인>\n");

            System.out.print(product.getPname() + "(");
            for (OptionsDTO opt : opts) {
                System.out.print(opt.getOptName());
                if (opt.getQuantity() != 0) {
                    System.out.print("(" + opt.getQuantity() + ")");
                }
                System.out.print("\t");
            }
            System.out.print(")" + "를 선택하셨습니다.\n");

            System.out.print("\n1. 주문 확정\t2.재선택\t3.상위 메뉴\t4.종료");

            choice = safeInput();

            System.out.println("\n=======================================\n");

            switch (choice) {
                case 1:
                    // 각 주문마다 새로운 객체 생성
                    addtionalOrder(ProductsDTO.builder()
                            .isBeverage(product.getIsBeverage())
                            .pno(product.getPno())
                            .price(product.getPrice())
                            .pname(product.getPname())
                            .options(new ArrayList<>(opts))
                            .build());
                    return; // 현재 옵션 선택 종료
                case 2:
                    System.out.println("다시 선택");
                    opts.clear(); // 기존 옵션 초기화
                    break;
                case 3:
                    CommonUI.displayGoBackMessage();
                    return;
                case 4:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }
        }

    }

    private void addtionalOrder(ProductsDTO product) {
        System.out.println("\n==================================================================\n");
        System.out.println("[주문 개수 선택]\n");

        System.out.print("\n1. 개수 선택\t2.상위 메뉴\t3.종료\n");

        int choice = safeInput();

        switch (choice) {
            case 1:
                displayOrderQuantity(product);
                return;
            case 2:
                CommonUI.displayGoBackMessage();
                return;
            case 3:
                CommonUI.displayExitMessage();
                System.exit(0);
            default:
                CommonUI.displayWrongSelectMessage();
        }
    }

    private void displayOrderQuantity(ProductsDTO product) {

        while (true) {
            System.out.println("\n==================================================================\n");
            System.out.println("[주문 개수 입력]\n");

            int choice = safeInput();;

            product.setQuantity(choice);
            System.out.print("\n1. 주문하기\t2.다른 상품/옵션 담기\t3.종료");

            choice = safeInput();

            if (order == null) {
                order = new ArrayList<>();
            }

            if (choice == 1) {
                order.add(product); // 주문 리스트에 추가
                timOrdersService.insertOrders(order, user.getUsersNo());
                order.clear(); // 초기화
                return;
            } else if (choice == 2) {
                order.add(product); // 주문 리스트에 추가
                CommonUI.displayGoBackMessage();
                return;
            } else if (choice == 3) {
                CommonUI.displayExitMessage();
                System.exit(0);
            } else {
                CommonUI.displayWrongSelectMessage();
            }
        }
    }


    // =================================== 3. 주문 취소 ===================================

    private void displayCancelOrders(OrderDetailsDTO orderDetail) {
        System.out.println("==================================================================\n");
        System.out.println("[주문 취소]\n");

        long diffMin = timOrdersService.getDiffMinOrderDateAndNow(orderDetail.getOrderDate());

        if (diffMin >= 5) {
            System.out.println("주문 시간 5분 경과하여 취소 불가합니다.");
        } else {
            if (timOrdersService.cancelOrderDetails(orderDetail) >= 1) {
                System.out.println("주문 취소에 성공했습니다.");
            } else {
                System.out.println("주문 취소에 실패했습니다.");
            }
        }

        System.out.println("\n1. 상위 메뉴\t2.종료\n");

        int choice = safeInput();

        if (choice == 1) {
            CommonUI.displayGoBackMessage();
        } else {
            CommonUI.displayExitMessage();
            System.exit(0);
        }

    }


    // =================================== 4. 주문 내역 조회 ===================================

    /**
     * 주문 내역 조회 초기 화면
     */
    private void viewOrdersHistory() {
        String[] menus = {
                "1. 전체 조회",
                "2. 기간별 조회",
                "3. 유저별 조회",
                "4. 사용자 정의 조회",
                "5. 상위 메뉴",
                "6. 종료"
        };


        while (true) {
            System.out.println("==================================================================");
            System.out.println("[ 주문 내역 조회 ]");
            System.out.println("==================================================================\n");

            System.out.printf(
                    "%-20s %-20s %-20s\n",
                    menus[0], menus[1], menus[2]
            );
            System.out.printf(
                    "%-20s %-20s %-20s\n",
                    menus[3], menus[4], menus[5]
            );

            System.out.println("\n==================================================================");

            int choice = safeInput();

            switch (choice) {
                case 1 -> this.displayAllOrdersHistory(1); // 전체 조회
                case 2 -> this.displayOrdersHistoryByPeriod(); // 기간별 조회
                case 3 -> this.displayOrdersHistoryByUsers(); // 유저별 조회
                case 4 -> this.displayOrdersHistoryByCustom(); // 사용자 정의 조회
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
        System.out.println("==================================================================\n");
        System.out.printf("현재 페이지: %d / %d\n", currentPage, totalPages);
        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-5s %-30s %-20s %-10s\n", "번호", "주문 날짜", "주문자", "결제 금액");
        System.out.println("------------------------------------------------------------------");

        int idx = 1;
        for (OrdersSelectDTO order : orders) {
            System.out.printf("%-5d %-30s %-20s %-10d\n", idx++, order.getOrderDate(), order.getUserName(), order.getTotalPrice());
        }
        System.out.println("------------------------------------------------------------------");

        CommonUI.displayPageBar(currentPage, totalPages);
    }


    /**
     * 주문 내역 목록 조회 메서드
     *
     * @param purpose   사용 목적 (1: 주문 조회, 2: 주문 취소)
     * @param type      조회 유형 (1: 전체, 2: 기간별, 3: 유저별, 4: 사용자 정의 기간, 5: 사용자 정의 기간 + 유저별 조회)
     * @param userNo    유저 번호 (유저별 조회 시 필요)
     * @param startDate 시작 날짜 (기간별 또는 사용자 정의 조회 시 필요)
     * @param endDate   종료 날짜 (사용자 정의 조회 시 필요)
     */
    private void displayOrdersHistory(int purpose, int type, Integer userNo, String startDate, String endDate) {
        int currPage = 1; // 현재 페이지
        int pageSize = 9; // 한 페이지에 표시할 주문 개수
        int totalSize = 0;
        List<OrdersSelectDTO> orders;

        while (true) {
            // 조회 데이터 가져오기
            switch (type) {
                case 1: // 전체 조회
                    totalSize = timOrdersService.selectOrdersListRownumAll();
                    orders = timOrdersService.selectOrderList(currPage, pageSize);
                    break;
                case 2:
                case 4:// 기간별 & 사용자 정의(기간만) 조회
                    totalSize = timOrdersService.selectOrdersListRownumByPeriod(startDate, endDate);
                    orders = timOrdersService.selectOrderListByPeriod(startDate, endDate, currPage, pageSize);
                    break;
                case 3: // 유저별 조회
                    if (userNo == null) {
                        System.out.println("유저 번호가 필요합니다.");
                        return;
                    }
                    totalSize = timOrdersService.selectOrdersListRownumByUsers(userNo);
                    orders = timOrdersService.selectOrdersListByUsersNo(userNo, currPage, pageSize);
                    break;
                case 5: // 사용자 정의 기간 + 유저별 조회
                    if (userNo == null) {
                        System.out.println("유저 번호가 필요합니다.");
                        return;
                    }
                    totalSize = timOrdersService.selectOrdersListRownumByCustomAndUsersNo(userNo, startDate, endDate);
                    orders = timOrdersService.selectOrderListByCustomAndUsersNo(userNo, startDate, endDate, currPage, pageSize);
                    break;
                default:
                    CommonUI.displayWrongSelectMessage();
                    return;
            }

            int totalPages = (int) Math.ceil((double) totalSize / pageSize); // 총 페이지 수 계산

            log.info("totalPages = {}", totalPages);

            // 결과 출력 및 페이지 처리
            while (true) {
                if (orders == null || orders.isEmpty()) {
                    this.displayEmptyMsg();
                    return;
                }

                this.printOrderList(orders, currPage, totalPages);

                int choice = selectPageMenu();

                switch (choice) {
                    case 10: // 다음 페이지
                        if (currPage < totalPages) currPage++;
                        break;
                    case 11: // 이전 페이지
                        if (currPage > 1) currPage--;
                        break;
                    case 12: // 페이지 직접 입력
                        System.out.print("이동할 페이지 번호를 입력하세요: ");
                        int newPage = sc.nextInt();
                        if (newPage > 0 && newPage <= totalPages) {
                            currPage = newPage;
                        } else {
                            System.out.println("유효하지 않은 페이지 번호입니다.");
                        }
                        break;
                    case 13: // 상위 메뉴
                        CommonUI.displayGoBackMessage();
                        return;
                    case 14: // 종료
                        CommonUI.displayExitMessage();
                        System.exit(0);
                    default:
                        if (choice >= 1 && choice <= orders.size()) { // 주문 상세 보기
                            this.displayOrdersDetail(purpose, orders.get(choice - 1));
                        } else {
                            CommonUI.displayWrongSelectMessage();
                        }
                }

                // 조회 데이터 갱신
                switch (type) {
                    case 1:
                        orders = timOrdersService.selectOrderList(currPage, pageSize);
                        break;
                    case 2:
                    case 4:
                        orders = timOrdersService.selectOrderListByPeriod(startDate, endDate, currPage, pageSize);
                        break;
                    case 3:
                        orders = timOrdersService.selectOrdersListByUsersNo(userNo, currPage, pageSize);
                        break;
                    case 5:
                        orders = timOrdersService.selectOrderListByCustomAndUsersNo(userNo, startDate, endDate, currPage, pageSize);
                        break;
                }
            }

        }
    }


    // -------------------------- 4.1. 전체 주문 내역 목록 출력 --------------------------

    /**
     * 전체 주문 내역 목록 출력
     * @param purpose   사용 목적 (1: 조회, 2: 주문 취소)
     */
    private void displayAllOrdersHistory(int purpose) {
        String[] menu = {"[전체 주문 내역 조회]", "[주문 취소]"};

        System.out.println("==================================================================\n");
        System.out.println(menu[purpose - 1] + "\n");

        this.displayOrdersHistory(purpose ,1, null, null, null);
    }


    // -------------------------- 4.2. 기간별 주문 내역 목록 출력 --------------------------

    /**
     * 기간별 주문 내역 목록 출력
     */
    private void displayOrdersHistoryByPeriod() {
        while (true) {
            System.out.println("==================================================================\n");
            System.out.println("[기간별 주문 내역 조회]\n");

            System.out.println("1. 연도별\t2. 월별\t3. 일자별\t4. 상위 메뉴\t5. 종료\n");

            int choice = safeInput();

            sc.nextLine(); // 버퍼 제거

            String startDate = null;
            String endDate = null;

            switch (choice) {
                case 1: // 연도별
                    System.out.println("\n조회할 연도를 입력해주세요 (예: 2024)\n");
                    System.out.print(">> ");
                    String input = sc.nextLine();
                    startDate = input + "-01-01";
                    endDate = input + "-12-31";
                    break;
                case 2: // 월별
                    System.out.println("\n조회할 연도와 월을 입력해주세요 (예: 2024-02)\n");
                    System.out.print(">> ");
                    String yearMonth = sc.nextLine();
                    startDate = yearMonth + "-01";
                    endDate = yearMonth + "-31"; // 정확한 일자는 서비스에서 처리
                    break;
                case 3: // 일자별
                    System.out.println("\n조회할 날짜를 입력해주세요 (예: 2024-02-28)\n");
                    System.out.print(">> ");
                    startDate = sc.nextLine();
                    endDate = startDate;
                    break;
                case 4: // 상위 메뉴
                    CommonUI.displayGoBackMessage();
                    return;
                case 5: // 종료
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
                    continue;
            }

            try {
                this.displayOrdersHistory(1,2, null, startDate, endDate);
            } catch (IllegalArgumentException e) {
                System.out.println(ANSIStyle.RED +  ">> [잘못된 날짜 형식을 입력하셨습니다. 다시 입력해주세요.]" + ANSIStyle.RESET);
                sc.nextLine(); // 버퍼 비우기
            }

        }
    }


    // -------------------------- 4.3. 유저별 주문 내역 목록 출력 --------------------------

    /**
     * 유저별 주문 내역 목록 출력
     */
    private void displayOrdersHistoryByUsers() {
        while (true) {
            System.out.println("==================================================================\n");
            System.out.println("[유저별 주문 내역 조회]\n");
            System.out.println("1. 유저 입력\t2. 상위 메뉴\t3. 종료\n");

            int choice = safeInput();

            switch (choice) {
                case 1 -> this.selectUsers(false, null, null);
                case 2 -> {
                    CommonUI.displayGoBackMessage();
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
        while (true) {
            System.out.println("==================================================================\n");
            System.out.println("[사용자 정의 주문 내역 조회]\n");

            System.out.println("1. 조회할 기간 입력\t2. 상위 메뉴\t3. 종료\n");

            int choice = safeInput();

            switch (choice) {
                case 1 -> {
                    System.out.println("—-------------------------------------------------------------\n");
                    System.out.println("시작 일자(ex. 2024-01-01)\n");
                    sc.nextLine();
                    System.out.print(">> ");
                    String startDate = sc.nextLine();
                    System.out.println("—-------------------------------------------------------------\n");
                    System.out.println("종료 일자(ex. 2024-01-01) \n");
                    System.out.print(">> ");
                    String endDate = sc.nextLine();
                    System.out.println("—-------------------------------------------------------------\n");
                    System.out.println("1. 전체 유저 조회\t2.유저별 조회\n");
                    System.out.print(">> ");

                    try{
                        int select = sc.nextInt();
                        if (select == 1 || select == 2) {
                            if (select == 1) {// 사용자 정의 방식으로 전체 유저 주문 내역 조회
                                this.displayOrdersHistory(1, 4, null, startDate, endDate);
                            } else { // 사용자 정의 방식으로 특정 유저 주문 내역 조회
                                this.selectUsers(true, startDate, endDate);
                            }
                        } else {
                            CommonUI.displayWrongSelectMessage();
                        }
                    }  catch (IllegalArgumentException e) {
                        System.out.println(">> [잘못된 날짜 형식입니다. 다시 입력해주세요.]");
                        sc.nextLine();
                    }
                }
                case 2 -> {
                    CommonUI.displayGoBackMessage();
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
     *
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

        while (true) {
            if (users == null) {
                this.displayEmptyMsg();
                return;
            }

            System.out.println("\n—-------------------------------------------------------------\n");
            System.out.println("조회할 유저를 선택해주세요\n");

            for (int i = 0; i < users.size(); i++) {
                UsersDTO user = users.get(i);
                System.out.println((i + 1) + ". " + user.getUsersName() + "(" + user.getUsersBirth() + ")");
            }

            System.out.println();

            CommonUI.displayPageBar(page, totalPages);

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
                    CommonUI.displayGoBackMessage();
                    return;
                }
                case 14 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> {
                    if (choice >= 1 && choice <= users.size()) { // users.size() = 9
                        int usersNo = users.get(choice - 1).getUsersNo();

                        if (!isCustom) { // 유저별 조회
                            this.displayOrdersHistory(1, 3, usersNo, null, null); // 세부 주문 내역 조회
                        } else { // 사용자 정의 조회
                            this.displayOrdersHistory(1, 5, usersNo, startDate, endDate); // 세부 주문 내역 조회
                        }
                    } else {
                        CommonUI.displayWrongSelectMessage();
                    }
                }
            }
        }
    }

    /**
     * 주문 세부 내역 조회
     *
     * @param purpose 사용 목적 (1: 조회, 2: 주문 취소)
     * @param order
     */
    private void displayOrdersDetail(int purpose, OrdersSelectDTO order) {
        while (true) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n").append("==================================================================").append("\n");
            sb.append("[주문 세부 내역]\n");
            OrderDetailsDTO orderDetail = timOrdersService.selectOrdersDetail(order.getOrderNo());

            if (orderDetail == null || orderDetail.getProducts().isEmpty()) {
                this.displayEmptyMsg();
            }

            orderDetail.setOrderDate(order.getOrderDate());
            sb.append("\n").append("[").append(orderDetail.getOrderDate()).append("]").append(" 주문 내역을 불러왔습니다.").append("\n");
            sb.append("----------------------------------------").append("\n");
            sb.append(orderDetail.getOrderDate()).append("\n");
            sb.append("----------------------------------------").append("\n");
            sb.append("주문번호 : ").append(order.getOrderNo()).append("\n");
            sb.append("----------------------------------------").append("\n");
            sb.append("주문자 : ").append(order.getUserName()).append("\n");
            sb.append("----------------------------------------").append("\n");

            for (ProductsDTO products : orderDetail.getProducts()) {
                sb.append(products.getPname()).append("\t").append(products.getPrice()).append("\t").append(products.getQuantity()).append("\n");
                if (products.getOptions() != null) {
                    for (OptionsDTO options : products.getOptions()) {
                        sb.append(options.getOptName()).append("\t").append(options.getPrice()).append("\t").append(options.getQuantity()).append("\n");
                    }
                }
            }

            sb.append("----------------------------------------").append("\n");
            sb.append("합계").append("\t\t\t").append(order.getTotalPrice()).append("\n");
            sb.append("----------------------------------------").append("\n");

            System.out.println(sb);

            // 목적별 추가 작업
            switch (purpose) {
                case 1: // 단순 조회
                    System.out.println("1. 상위 메뉴\t2. 종료");
                    break;
                case 2: // 주문 취소
                    System.out.println("1. 주문 취소\t2. 상위 메뉴\t3. 종료");
                    break;
                default:
                    System.out.println("알 수 없는 목적입니다.");
                    return;
            }

            int select = safeInput();

            if (purpose == 1) { // 조회 목적
                if (select == 1) {
                    CommonUI.displayGoBackMessage();
                    return;
                } else if (select == 2) {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                } else {
                    CommonUI.displayWrongSelectMessage();
                }
            } else if (purpose == 2) { // 주문 취소 목적
                if (select == 1) {
                    this.displayCancelOrders(orderDetail);
                } else if (select == 2) {
                    CommonUI.displayGoBackMessage();
                    return;
                } else if (select == 3) {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                } else {
                    CommonUI.displayWrongSelectMessage();
                }
            } else {
                CommonUI.displayWrongSelectMessage();
                return;
            }
        }
    }


    /**
     * 주문 내역 목록 조회 선택 메뉴 출력
     *
     * @return
     */
    private int selectPageMenu() {
        String[] str = {"10. 다음 페이지", "11. 이전 페이지", "12. 페이지 입력", "13. 상위 메뉴", ANSIStyle.RED + "14. 시스템 종료" + ANSIStyle.RESET};

        for (int i = 0; i < str.length; i++) {
            System.out.printf("%-20s", str[i]);

            if ((i+1) % 3 == 0) {
                System.out.println();
            }
        }
        System.out.println("==================================================================");
        return safeInput();
    }


    // =================================== 5. 품목 판매 일시 중지 ===================================

    private void displayPauseSale(ProductsDTO product){

        while (true) {
            System.out.println("==================================================================\n");
            System.out.println("[품목 판매 중지]\n");

            System.out.println(product.getPname());

            if (product.isActive()){
                System.out.println("\n1. 판매 중지");
            } else {
                System.out.println("\n1. 판매 중지 해제");
            }

            System.out.println("\t2. 상위 메뉴\t3.종료\n");
            int choice = safeInput();

            if (choice == 1) {
                int res = timOrdersService.updateProductsIsActive(product.getPno(), product.isActive());
                if (res == 1) {
                    System.out.println("판매 중지 성공");
                } else {
                    System.out.println("판매 중지 실패");
                }
                return;
            } else if (choice == 2) {
                CommonUI.displayGoBackMessage();
                return;
            } else if (choice == 3) {
                CommonUI.displayExitMessage();
                System.exit(0);
            } else {
                CommonUI.displayWrongSelectMessage();
            }
        }
    }

    // =================================== 6. 유틸 ==============================================

    private void displayEmptyMsg() {
        while (true) {
            System.out.println("\n-----------------------------------\n");
            System.out.println("내용이 존재하지 않습니다.");

            System.out.println("1. 상위 메뉴\t2.종료\n");

            int choice = safeInput();

            if (choice == 1) {
                CommonUI.displayGoBackMessage();
                return;
            } else if (choice == 2) {
                CommonUI.displayExitMessage();
                System.exit(0);
            } else {
                CommonUI.displayWrongSelectMessage();
            }
        }
    }

    /**
     * 숫자만 입력받을 수 있도록 예외처리
     * @return
     */
    private int safeInput() {
        int input = 0;

        while (true) {
            try {
                System.out.print(">> ");
                input = sc.nextInt(); // 입력값 받기
                break; // 유효한 입력값이면 반복 종료
            } catch (InputMismatchException e) {
                System.out.println(ANSIStyle.RED + ">> [숫자만 입력 가능합니다. 다시 시도해주세요.]" + ANSIStyle.RESET);
                sc.nextLine(); // 버퍼 정리
            }
        }

        return input;
    }

}
