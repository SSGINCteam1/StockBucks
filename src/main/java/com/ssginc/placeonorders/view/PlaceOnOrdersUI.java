package com.ssginc.placeonorders.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.placeonorders.model.dao.PlaceOnOrdersDAO;
import com.ssginc.placeonorders.model.dto.PlaceOnOrdersHistoryDTO;
import com.ssginc.placeonorders.model.dto.PlaceOnOrdersInsertBagDTO;
import com.ssginc.placeonorders.model.dto.PlaceonOrdersCheckDTO;
import com.ssginc.placeonorders.model.dto.PlaceonOrdersDTO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersStockVO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersVO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class PlaceOnOrdersUI {
    private final Scanner sc;
    private final PlaceOnOrdersDAO dao;
    private final DataSource dataSource;
    private final UsersDTO user;

    public PlaceOnOrdersUI(UsersDTO user) {
        sc = new Scanner(System.in);
        dao = new PlaceOnOrdersDAO();
        dataSource = dao.dataSource;
        this.user = user;
    }

    // 발주
    public void PlaceOnOrderschoice() {
        boolean running = true;

        while (running) {
            System.out.println("===================================");
            System.out.println("[발주]");
            System.out.println("1.지점 재고 조회\t2. 발주 가능 품목 조회\t3. 발주 신청\t4. 발주 수정 및 취소\t5. 발주 내역 조회\n" +
                    "6.종료\n");
            System.out.print(">> ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    break;
                case 2:
                    displayOrderableStocks();
                    break;
                case 3:
                    break;
                case 4:
                    UpdateDeleteOrderableStock();
                    break;
                case 5:
                    displayHistoryStocks();
                    break;
                case 6:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    System.out.println("잘못된 입력입니다. 1~6 사이의 숫자를 입력해주세요.");
            }
        }
    }


    //  발주 가능 품목 조회
    public void displayOrderableStocks() {
        boolean running = true;

        while (running) {
            System.out.println("===================================");
            System.out.println("[발주 가능 품목 조회]");
            System.out.println("1. 전체 조회\t2. 카테고리 조회\t3. 검색\t4.선택 및 장바구니 담기\t5. 상위 메뉴\t6. 종료");
            System.out.print(">> ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    displayAllOrderableStocks();
                    break;
                case 2:
                    displayAllOrderableStocksbyCategory();
                    break;
                case 3:
                    displayAllOrderableStocksbyKeyword();
                    break;
                case 4:
                    PlaceOnOrdersInsertBag();
                    break;
                case 5:
                    return;
                case 6:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    System.out.println("잘못된 입력입니다. 1~6 사이의 숫자를 입력해주세요.");
            }
        }
    }

    // 발주 내역 조회
    public void displayHistoryStocks() {
        System.out.println("===================================");
        System.out.println("[발주 내역 조회]");
        System.out.println("1. 전체 조회\t2. 조건부 조회\t3. 상위 메뉴\t4. 종료");
        System.out.print(">> ");

        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                displayAllHistoryOrderableStocks();
                break;
            case 2:
                displayHistoryOrderableStocksByCondition();
                break;
            case 3:
                PlaceOnOrderschoice();
            case 4:
                CommonUI.displayExitMessage();
                System.exit(0);
            default:
                System.out.println("잘못된 입력입니다. 1~4 사이의 숫자를 입력해주세요.");
        }
    }


    // 전체 발주 가능 품목 조회 구현
    public void displayAllOrderableStocks() {
        try (Connection con = dataSource.getConnection()) {
            ArrayList<PlaceonOrdersDTO> stocks = dao.selectAllOrderableStocks(con);
            if (stocks.isEmpty()) {
                System.out.println("발주 가능한 품목이 없습니다.");
                displayOrderableStocks();
            } else {
                for (PlaceonOrdersDTO stock : stocks) {
                    System.out.println(stock);
                }
            }
        } catch (SQLException e) {
            System.out.println("발주 가능 품목 조회 중 오류: " + e.getMessage());
        }
    }

    // 카테고리별 조회 구현
    public void displayAllOrderableStocksbyCategory() {
        boolean running = true;

        while (running) {
            System.out.println("===================================\n");
            System.out.println("[카테고리 조회] \n");
            System.out.println("1. 디저트\t2. MD\t3. 일회용품\t4. 원자재\t5. 병음료\t6. 원두\n" +
                    "\t7. 상위 메뉴로\t8. 종료");
            System.out.print(">> ");

            int a = sc.nextInt();
            if (a == 7) {
                displayOrderableStocks();
                break;
            } else if (a == 8) {
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
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    // 키워드로 검색 구현
    public void displayAllOrderableStocksbyKeyword() {

        System.out.println("===================================\n");
        System.out.println("검색하실 키워드를 입력해주세요");
        System.out.print(">> ");
        String keyword = sc.next();
        try (Connection con = dataSource.getConnection()) {
            ArrayList<PlaceonOrdersDTO> stocks = dao.selectAllOrderableStocksByKeyword(con, keyword);

            if (stocks.isEmpty()) {
                System.out.println("키워드에 맞는 조회 가능한 재고가 없습니다.");
            } else {
                System.out.println("전체 재고 목록 :");
                for (PlaceonOrdersDTO stock : stocks) {
                    System.out.println(stock);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // 조건부 조회
    public void displayHistoryOrderableStocksByCondition() {
        boolean running = true;
        while (running) {
            System.out.println("===================================\n");
            System.out.println("[조건부 조회] \n");
            System.out.println("1. 기간별 조회\t2. 품목별 조회\t3. 상위 메뉴\t4. 종료\n");
            System.out.print(">> ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("미구현");
                    break;
                case 2:
                    displayHistoryOrderableStocksByCategory();
                    break;
                case 3:
                    displayHistoryStocks();
                    break;
                case 4:
                    CommonUI.displayExitMessage();
                    System.exit(0);
            }
        }
    }

    // 선택 및 장바구니 담기
    public void PlaceOnOrdersInsertBag() {
        boolean running = true;
        while (running) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("===================================");
                displayAllOrderableStocks();

                System.out.print("선택하실 품목의 고유번호를 입력해주세요: ");
                int stNo = sc.nextInt();

                System.out.print("수량을 입력해주세요: ");
                int pobQuantity = sc.nextInt();

                System.out.print("장바구니에 담으시겠습니까? (Y/N): ");
                String a = sc.next();

                if (a.equals("Y")) {
                    PlaceOnOrdersInsertBagDTO dto = new PlaceOnOrdersInsertBagDTO();
                    dto.setUsers_no(this.user.getUsersNo());
                    dto.setStNo(stNo);
                    dto.setPobQuantity(pobQuantity);

                    dao.placeOnOrdersInsertBag(con, dto);
                    System.out.println("장바구니에 추가되었습니다.");
                } else {
                    System.out.println("취소되었습니다.");
                }

            } catch (SQLException e) {
                System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());
            }
            System.out.println("1.물품 추가\t2.돌아가기\n ");
            System.out.print(">> ");
            int x = sc.nextInt();

            if (x == 2)
                break;
        }
        PlaceOnOrderschoice();

    }


    // 발주 내역 조회
    public void displayAllHistoryOrderableStocks() {
        try (Connection con = dataSource.getConnection()) {
            ArrayList<PlaceonOrdersCheckDTO> stocks = dao.selectAllOrderableStockChecks(con);

            if (stocks.isEmpty()) {
                System.out.println("발주 내역이 없습니다.");
                displayOrderableStocks();
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


    // 발주내역 카테고리 조회
    public void displayHistoryOrderableStocksByCategory() {
    try(Connection con = dataSource.getConnection()) {
        System.out.println("===================================\n");
        System.out.println("카테고리 조회 \n");
        System.out.println("1. 디저트\t2. MD\t3. 일회용품\t4. 원자재\t5. 병음료\t6. 원두\n" +
                "\t7. 상위 메뉴로\t8. 종료");
        System.out.print(">> ");
        int choice = sc.nextInt();

        // 입력 값에 따른 카테고리 설정
        String category = null;
        switch (choice) {
            case 1: category = "디저트"; break;
            case 2: category = "MD"; break;
            case 3: category = "일회용품"; break;
            case 4: category = "원자재"; break;
            case 5: category = "병음료"; break;
            case 6: category = "원두"; break;
            case 7: return; // 상위 메뉴로
            case 8:
                CommonUI.displayExitMessage();
                System.exit(0);// 종료
            default:
                System.out.println("잘못된 입력입니다.");
                return;
        }
        ArrayList<PlaceOnOrdersHistoryDTO> stocks = dao.HistoryplaceOrdersStockByCategory(con,choice);

        if (stocks.isEmpty()) {
            System.out.println("발주 내역이 없습니다.");
        } else {
            for (PlaceOnOrdersHistoryDTO stock : stocks) {
                System.out.println(stock);
            }
        }
    }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //발주 수정 및 취소
    public void UpdateDeleteOrderableStock() {
        boolean running = true;
        while (running) {
                System.out.println("===================================");
                System.out.println("발주 수정 및 취소 \n");
                System.out.println("1. 발주 수정\t2. 발주 취소\t3. 상위 메뉴로\t4. 종료");
                System.out.print(">> ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        UpdateOrderableStock();
                        break;
                    case 2:
                        DeleteOrderableStock();
                        break;
                    case 3:
                        return;
                    case 4:
                        CommonUI.displayExitMessage();
                        System.exit(0);
                        break;
                }
        }
    }
    // 발주 취소 구현
    public void DeleteOrderableStock() {

            try (Connection con = dataSource.getConnection()) {
                System.out.println("===================================");
                System.out.print(">> ");
                displayAllHistoryOrderableStocks();


                System.out.println("삭제하실 품목의 고유번호를 입력해주세요: ");
                int stNo = sc.nextInt();

                System.out.print("정말 삭제하시겠습니까? (Y/N): ");
                String a = sc.next();

                if (a.equals("Y")) {
                    PlaceOrdersStockVO vo2 = new PlaceOrdersStockVO();
                    PlaceOrdersVO vo1 = new PlaceOrdersVO();

                    vo2.setPoNo(stNo);
                    vo1.setPoNo(stNo);
                    dao.DeleteOrderHistory2(con, vo2);
                    dao.DeleteOrderHistory(con,vo1);

                    System.out.println("품목이 삭제되었습니다.");
                } else {
                    System.out.println("취소되었습니다.");
                }

            } catch (SQLException e) {
                System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());

        }
    }

    // 발주 수정 구현
    public void UpdateOrderableStock() {

        try (Connection con = dataSource.getConnection()) {
            System.out.println("===================================");
            displayAllHistoryOrderableStocks();


            System.out.println("수정하실 품목의 고유번호를 입력해주세요: ");
            System.out.print(">> ");
            int stNo = sc.nextInt();
            System.out.println("변경하실 수량을 입력해주세요: ");
            System.out.print(">> ");
            int postQuantity = sc.nextInt();

            PlaceOrdersStockVO vo = new PlaceOrdersStockVO();
            vo.setPoNo(stNo);
            vo.setPostQuantity(postQuantity);
            dao.UpdateOrderHistory(con, vo);
            System.out.println("품목이 수정되었습니다.");



        } catch (SQLException e) {
            System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());

        }
    }
}













