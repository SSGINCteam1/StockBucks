//package com.ssginc.orders.view;
//
//import com.ssginc.common.view.CommonUI;
//import com.ssginc.login.model.dto.UsersDTO;
//import com.ssginc.login.model.dto.UsersDTOTest;
//import com.ssginc.orders.model.dao.PrdCgDAO;
//import com.ssginc.orders.model.dao.PrdOptDAO;
//import com.ssginc.orders.model.dao.ProductsDAO;
//import com.ssginc.orders.model.dao.StockDAO;
//import com.ssginc.orders.model.dto.*;
//import com.ssginc.orders.service.TimOrdersService;
//import com.ssginc.orders.service.WishOrdersService;
//import com.ssginc.orders.service.WishOrdersServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.*;
//
//@Slf4j
//public class WishOrdersUI {
//    private final Scanner sc;
//    private final PrdCgDAO prdCgDAO;
//    private final StockDAO stockDAO;
//    private final ProductsDAO productsDAO;
//    private final PrdOptDAO prdOptDAO;
//    private final WishOrdersService wishOrdersService;
//    private final UsersDTO user;
//
//    public WishOrdersUI(UsersDTO user) {
//        this.sc = new Scanner(System.in);
//        this.prdCgDAO = new PrdCgDAO();
//        this.stockDAO = new StockDAO();
//        this.productsDAO = new ProductsDAO();
//        this.prdOptDAO = new PrdOptDAO();
//        this.wishOrdersService = new WishOrdersServiceImpl();
//        this.user = user;
//    }
//
//    private final String[] MENU = {"음료", "푸드", "상품", "상위 메뉴", "종료"};
//
//    private static List<WishProductsDTO> order;
//
//
//    public void orderMenu() {
//
//        while (true) {
//            System.out.println("<주문>\n");
//            System.out.println("1. 품목 조회\t2. 품목 주문\t3. 주문 취소\t4. 주문 내역 조회\t5. 품목 판매 일시 중지");
//            System.out.print(">> ");
//            int select = sc.nextInt();
//            System.out.println("\n=======================================\n");
//            if (select == 1) {
//                this.selectBigCategory(true);
//            } else if (select == 2) {
//                this.selectBigCategory(false);
////                System.out.println("<품목 주문>\n");
////                ArrayList<WishProductsDTO> products = productsDAO.selectProductsListAll();
////                for (int i = 0; i < products.size(); i++) {
////                    System.out.println((i + 1) + ". " + products.get(i).getPname() + " - " + products.get(i).getPrice());
////                }
////                System.out.print("\n음료를 선택하세요>> ");
////                this.selectBeverageOption();
//            }
//            System.out.println("\n=======================================\n");
//        }
//    }
//
//    private void selectBigCategory(boolean isView) {
//        order = new ArrayList<>();
//
//        ArrayList<StockDTO> stock1 = stockDAO.selectFood();
//        ArrayList<StockDTO> stock2 = stockDAO.selectMd();
//
//        String menu = isView ? "<대분류 조회>\n" : "<품목 주문>\n";
//        while (true) {
//            System.out.println(menu);
//            for (int i = 0; i < MENU.length; i++) {
//                System.out.print((i + 1) + ". \t" + MENU[i] + "\t");
//            }
//            System.out.print("\n>> ");
//            int choice = sc.nextInt();
//
//            System.out.println("\n=======================================\n");
//
//            if (choice == 1) {
//                this.selectBeverageCategory(isView);
//            } else if (choice == 2) {
//                menu = isView ? "<푸드 조회>\n" : "<푸드 주문>\n";
//                System.out.println(menu);
//                for (int i = 0; i < stock1.size(); i++) {
//                    System.out.println((i + 1) + ". " + stock1.get(i).getStName() + "\t");
//                }
//                System.out.println("\n=======================================\n");
//
//            } else if (choice == 3) {
//                menu = isView ? "<상품 조회>\n" : "<상품 주문>\n";
//                System.out.println(menu);
//                for (int i = 0; i < stock2.size(); i++) {
//                    System.out.println((i + 1) + ". " + stock2.get(i).getStName() + "\t");
//                }
//                System.out.println("\n=======================================\n");
//
//            } else if (choice == 4) {
//                return;
//            } else if (choice == 5) {
//                CommonUI.displayExitMessage();
//                System.exit(0);
//            }
//        }
//    }
//
//
//    /**
//     * 음료 카테고리 조회
//     */
//    private void selectBeverageCategory(boolean isView) {
//        ArrayList<PrdCgDTO> prdCg = prdCgDAO.selectPrdCg();
//        String menu = isView ? "<음료 조회>\n" : "<음료 주문>\n";
//        while (true) {
//            System.out.println(menu);
//            for (int i = 0; i < prdCg.size(); i++) {
//                System.out.print((i + 1) + ". \t" + prdCg.get(i).getPrdCgName() + "\t");
//            }
//
//            System.out.print((prdCg.size() + 1) + ". \t" + "병음료" + "\t");
//            System.out.print("\n>> ");
//            int choice2 = sc.nextInt();
//
//            System.out.println("\n=======================================\n");
//
//            System.out.println("선택한 번호 : " + choice2);
//
//            ArrayList<WishProductsDTO> products = null;
//
//            if (isView){
//                products = productsDAO.selectProductsListByPrdcgNo(choice2, true);
//            } else {
//                products = productsDAO.selectProductsListByPrdcgNo(choice2, false);
//            }
//
//            switch (choice2) {
//                case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11:
//                    selectBeverageMenu(isView, prdCg.get(choice2 - 1).getPrdCgName(), products);
//                    break;
//                case 12:
//                    return;
//                default:
//            }
//            System.out.println("\n=======================================\n");
//        }
//    }
//
//    private void selectBeverageMenu(boolean isView, String prdcgName, ArrayList<WishProductsDTO> products) {
//        int preMenu = products.size() + 1;
//        int exit = products.size() + 2;
//
//        String menu = isView ? "조회>\n" : "주문>\n";
//
//        while (true) {
//            System.out.println("<" + prdcgName + " " + menu);
//            for (int i = 0; i < products.size(); i++) {
//                System.out.println((i + 1) + ". " + products.get(i).getPname() + " - " + products.get(i).getPrice() + "원");
//            }
//            System.out.print(preMenu + ". \t" + "상위 메뉴");
//            System.out.print("\t" + exit + ". \t" + "종료");
//            System.out.print("\n>> ");
//            int choice = sc.nextInt();
//            System.out.println("\n선택한 번호 : " + choice);
//            System.out.println("\n=======================================\n");
//
//            if (choice >= 1 && choice < preMenu) {
//                selectBeverageOption(products.get(choice - 1));
//            }else if (choice == preMenu) {
//                return;
//            } else if (choice == exit) {
//                CommonUI.displayExitMessage();
//                System.exit(0);
//            }
//
//            System.out.println("\n=======================================\n");
//        }
//    }
//
//    private void selectBeverageOption(WishProductsDTO product) {
//        product.setIsBeverage(1);
//
//        List<OptionsDTO> opts = new ArrayList<>();
//
//        ArrayList<PrdOptDTO> prdopt = prdOptDAO.selectPrdOpt(product.getPno());
//
//        System.out.println("\n=======================================\n");
//
//        while (true){
//            System.out.println("<옵션 선택>\n");
//
//
//            System.out.println("\n1. 옵션 선택\t2.다른 상품 담기\t3.종료\n");
//            System.out.print(">> ");
//            int choice = sc.nextInt();
//
//            if (choice != 1) {
//                if (choice == 2) {
//                    CommonUI.displayGoBackMessage();
//                    return;
//                } else if (choice == 3) {
//                    CommonUI.displayExitMessage();
//                    System.exit(0);
//                } else {
//                    CommonUI.displayWrongSelectMessage();
//                    continue;
//                }
//            }
//
//            for (int i = 0; i < prdopt.size(); i++) {
//                System.out.println((i + 1) + ". "
//                        + prdopt.get(i).getOptCategoryName());
//
//                List<PrdOptDetailDTO> details = prdopt.get(i).getOptionDetails();
//
//                for (int j = 0; j < details.size(); j++) {
//                    System.out.print("\t" + (j + 1) + ". "
//                            + details.get(j).getOptionName());
//                }
//
//
//                System.out.print("\n>> ");
//                choice = sc.nextInt();
//
//                PrdOptDetailDTO opt = null;
//
//                if ( choice >= 1 && details.size() == 1){
//                    opt = details.get(0);
//                    opts.add(OptionsDTO.builder()
//                                    .optNo(opt.getOptNo())
//                                    .optName(opt.getOptionName())
//                                    .quantity(choice)
//                                    .price(opt.getOptionPrice())
//                            .build());
//                } else if (choice >= 1 && choice <= details.size()) {
//                    opt = details.get(choice - 1);
//                    opts.add(OptionsDTO.builder()
//                                .optNo(opt.getOptNo())
//                                .optName(opt.getOptionName())
//                                .quantity(0)
//                                .price(opt.getOptionPrice())
//                            .build());
//                }
//
//                System.out.println();
//            }
//
//            System.out.println("\n=======================================\n");
//            System.out.println("<주문 확인>\n");
//
//            System.out.print(product.getPname() + "(");
//            for (OptionsDTO opt : opts) {
//                System.out.print(opt.getOptName());
//                if (opt.getQuantity() != 0){
//                    System.out.print("(" + opt.getQuantity() + ")");
//                }
//                System.out.print("\t");
//            }
//            System.out.print(")" + "를 선택하셨습니다.\n");
//
//            System.out.print("\n1. 주문 확정\t2.재선택\t3.상위 메뉴\t4.종료" );
//
//            System.out.print("\n옵션을 선택하세요.>> ");
//            choice = sc.nextInt();
//
//            System.out.println("\n=======================================\n");
//
//            switch (choice){
//                case 1:
//                    // 각 주문마다 새로운 객체 생성
//                    WishProductsDTO newOrder = new WishProductsDTO();
//                    newOrder.setIsBeverage(product.getIsBeverage());
//                    newOrder.setPno(product.getPno());
//                    newOrder.setPrice(product.getPrice());
//                    newOrder.setPname(product.getPname());;
//                    newOrder.setOptions(new ArrayList<>(opts));
//                    addtionalOrder(newOrder);
//                    return; // 현재 옵션 선택 종료
//                case 2:
//                    System.out.println("다시 선택");
//                    opts.clear(); // 기존 옵션 초기화
//                    break;
//                case 3:
//                    CommonUI.displayGoBackMessage();
//                    return;
//                case 4:
//                    CommonUI.displayExitMessage();
//                    System.exit(0);
//                default:
//                    CommonUI.displayWrongSelectMessage();
//            }
//        }
//
//    }
//
//    private void addtionalOrder(WishProductsDTO product){
//        System.out.println("\n=======================================\n");
//        System.out.println("[추가 주문]\n");
//
//        System.out.print("\n1. 개수 선택\t2.상위 메뉴\t3.종료" );
//
//        System.out.print("\n>> ");
//        int choice = sc.nextInt();
//
//        switch (choice){
//            case 1:
//                displayOrderQuantity(product);
//                break;
//            case 2:
//                CommonUI.displayGoBackMessage();
//                return;
//            case 3:
//                CommonUI.displayExitMessage();
//                System.exit(0);
//            default:
//                CommonUI.displayWrongSelectMessage();
//        }
//    }
//
//
//    private void displayOrderQuantity(WishProductsDTO product){
//        while (true){
//
//            System.out.println("\n=======================================\n");
//            System.out.println("[주문 개수 입력]\n");
//
//            System.out.print("\n>> ");
//            int choice = sc.nextInt();
//
//            product.setQuantity(choice);
//            System.out.print("\n1. 주문하기\t2.다른 상품/옵션 담기\t3.종료" );
//
//            System.out.print("\n>> ");
//            choice = sc.nextInt();
//
//            if (choice == 1){
//                order.add(product);
//                wishOrdersService.insertOrders(order, user.getUsersNo());
//                break;
//            } else if (choice == 2){
//                order.add(product);
//                CommonUI.displayGoBackMessage();
//                return;
//            } else if (choice == 3) {
//                CommonUI.displayExitMessage();
//                System.exit(0);
//            } else {
//                CommonUI.displayWrongSelectMessage();
//            }
//        }
//    }
//
//}
