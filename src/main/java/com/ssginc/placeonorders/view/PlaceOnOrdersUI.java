package com.ssginc.placeonorders.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.placeonorders.model.dao.PlaceOnOrdersDAO;
import com.ssginc.placeonorders.model.dto.PlaceonOrdersCheckDTO;
import com.ssginc.placeonorders.model.dto.PlaceonOrdersDTO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class PlaceOnOrdersUI {
    Scanner sc;
    PlaceOnOrdersDAO dao = new PlaceOnOrdersDAO();
    public DataSource dataSource;

    public PlaceOnOrdersUI() {
        sc = new Scanner(System.in);
        dataSource = dao.dataSource;
    }

    public void PlaceOnOrderschoice() {
        boolean running = true;

        while (running) {
            int choice = PlaceOnOrders(sc);

            switch (choice) {
                case 1:

                    // 재고 조회 로직
                    break;
                case 2:

                    // 재고 신청 로직
                    break;
                case 3:
                    displayHistoryStocks();
                    // 발주 내역 조회 로직
                    break;
                case 4:
                    displayOrderableStocks();
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
    }

    public int PlaceOnOrders(Scanner sc) {
        System.out.println("===================================\n");
        System.out.println("[발주] \n");
        System.out.println("1. 재고 조회\t2. 재고 신청\t 3. 발주 내역 조회\t4.발주 가능 품목 조회\n5. 종료\n");
        System.out.print(">> ");

        return sc.nextInt();
    }


    //  발주 가능 품목 조회

    public void displayOrderableStocks() {
        boolean running = true;

        while (running) {
            System.out.println("===================================\n");
            System.out.println("[발주 가능 품목 조회] \n");
            System.out.println("1. 전체 조회\t2. 카테고리 조회\t3. 검색\t4. 품목 선택\t5. 상위 메뉴\t6. 종료");
            System.out.print(">> ");

            int a = sc.nextInt();

            try {
                switch (a) {
                    case 1:
                        displayAllOrderableStocks();
                        // 발주가능한 모든 품목 조회
                        break;
                    case 2:
                        displayAllOrderableStocksbyCategory();
                        // 발주가능한 모든 품목 카테고리로 조회
                        break;
                    case 3:
                        // 검색
                        displayAllOrderableStocksbyKeyword();
                        break;
                    case 4:
                        // 품목 선택
                        break;
                    case 5:
                        PlaceOnOrderschoice();
                        break;
                    case 6:
                        CommonUI.displayExitMessage();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("잘못된 입력입니다. 1~6 사이의 숫자를 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
            }
        }
    }

    public void displayHistoryStocks() {
        boolean running = true;

        while (running) {
            System.out.println("===================================\n");
            System.out.println("발주내역 조회 \n");
            System.out.println("1. 전체 조회\t2. 조건부 조회\t3. 상위 메뉴로 돌아가기\t4.종료");
            System.out.print(">> ");
            int a = sc.nextInt();

            switch (a) {
            case 1:
                displayAllHistoryOrderableStocks();
                break;
            case 2:

                break;
            case 3:
                PlaceOnOrderschoice();
                break;
            case 4:
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            }
        }


    }


    public void displayAllOrderableStocks() {
        try (Connection con = dataSource.getConnection()) {
            ArrayList<PlaceonOrdersDTO> stocks = dao.selectAllOrderableStocks(con);
            if(stocks.isEmpty()) {
                System.out.println("조회 가능한 재고가 없습니다.");
            }
            else {
                System.out.println("전체 재고 목록 :");
                for(PlaceonOrdersDTO stock : stocks) {
                    System.out.println(stock);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    // 카테고리별 조회
    public void displayAllOrderableStocksbyCategory() {
        boolean running = true;

        while (running) {
            System.out.println("===================================\n");
            System.out.println("카테고리 조회 \n");
            System.out.println("1. 디저트\t2. MD\t3. 일회용품\t4. 원자재\t5. 병음료\t6. 원두\n" +
                            "\t7. 상위 메뉴로\t8. 종료");
            System.out.print(">> ");

            int a = sc.nextInt();
            if(a == 7) {
                PlaceOnOrderschoice();
                break;
            }
            else if(a == 8) {
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            }
            try (Connection con = dataSource.getConnection()) {
                ArrayList<PlaceonOrdersDTO> stocks = dao.selectAllOrderableStocksByCategory(con, a);

                if (stocks.isEmpty()) {
                    System.out.println("조회 가능한 재고가 없습니다.");
                } else {
                    System.out.println("전체 재고 목록 :");
                    for (PlaceonOrdersDTO stock : stocks) {
                        System.out.println(stock);
                    }
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void displayAllOrderableStocksbyKeyword() {

            System.out.println("===================================\n");
            System.out.println("검색하실 키워드를 입력해주세요");
            System.out.print(">> ");
            String keyword = sc.next();
            try(Connection con = dataSource.getConnection()) {
                ArrayList<PlaceonOrdersDTO> stocks = dao.selectAllOrderableStocksByKeyword(con, keyword);

                if (stocks.isEmpty()) {
                    System.out.println("키워드에 맞는 조회 가능한 재고가 없습니다.");
                } else {
                    System.out.println("전체 재고 목록 :");
                    for (PlaceonOrdersDTO stock : stocks) {
                        System.out.println(stock);
                    }
                }
            }

            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    public void displayAllHistoryOrderableStocks() {
        try (Connection con = dataSource.getConnection()) {
            ArrayList<PlaceonOrdersCheckDTO> stocks = dao.selectAllOrderableStockChecks(con);

            if (stocks.isEmpty()) {
                System.out.println("발주 내역이 없습니다.");
            } else {
                for (PlaceonOrdersCheckDTO stock : stocks) {
                    System.out.println(stock);
                }
            }

        } catch (Exception e) {
            System.out.println("알 수 없는 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

}



