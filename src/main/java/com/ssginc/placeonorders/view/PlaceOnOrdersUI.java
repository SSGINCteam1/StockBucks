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
                    "6.상위메뉴\t7.종료\n");
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
                    CommonUI.displayGoBackMessage();
                    return;
                case 7:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();

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
                    displayAllOrderableStocks(1);
                    break;
                case 2:
                    displayAllOrderableStocksbyCategory();
                    break;
                case 3:
                    displayAllOrderableStocksbyKeyword();
                    break;
                case 4:
                    PlaceOnOrdersInsertBag(1);
                    break;
                case 5:
                    CommonUI.displayGoBackMessage();
                    return;
                case 6:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }
        }
    }

    // 발주 내역 조회
    public void displayHistoryStocks() {
        boolean running = true;

        while (running) {
            System.out.println("===================================");
            System.out.println("[발주 내역 조회]");
            System.out.println("1. 전체 조회\t2. 조건부 조회\t3. 상위 메뉴\t4. 종료");
            System.out.print(">> ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    displayAllHistoryOrderableStocks(1);
                    break;
                case 2:
                    displayHistoryOrderableStocksByCondition();
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


    // 전체 발주 가능 품목 조회 구현
    public void displayAllOrderableStocks(int i) {
        boolean running = true;

        while (running) {
            try (Connection con = dataSource.getConnection()) {
                ArrayList<PlaceonOrdersDTO> stocks = dao.selectAllOrderableStocks(con);
                if (stocks.isEmpty()) {
                    System.out.println("발주 가능한 품목이 없습니다.");
                    displayOrderableStocks();
                } else {
                    for (PlaceonOrdersDTO stock : stocks) {
                        System.out.println(stock);
                    }
                    if(i==0)
                    {
                        break;
                    }
                }
            } catch (SQLException e) {
                System.out.println("발주 가능 품목 조회 중 오류: " + e.getMessage());
            }
            if(i == 1)
            {
            System.out.println("1.상위 메뉴\t2. 종료");
            int a = sc.nextInt();
            switch (a) {
                case 1:
                    CommonUI.displayGoBackMessage();
                    return;
                case 2:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }
            }

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
                CommonUI.displayGoBackMessage();
                return;
            } else if (a == 8) {
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            }
            if (a != 1 && a != 2 && a != 3 && a != 4 && a != 5 && a != 6 && a != 7 && a != 8) {
                CommonUI.displayWrongSelectMessage();
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
        boolean running = true;

        while (running) {
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


            System.out.print("1.상위 메뉴\t2. 종료\t3.다시 조회\n");
            System.out.println(">>");
            int a = sc.nextInt();
            switch (a) {
                case 1:
                    CommonUI.displayGoBackMessage();
                    return;
                case 2:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                case 3:
                    break;
                default:
                    CommonUI.displayWrongSelectMessage();
            }
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
                    PlaceOnOrdersHistoryByPeriod();
                break;
                case 2:
                    displayHistoryOrderableStocksByCategory();
                    break;
                case 3:
                    CommonUI.displayGoBackMessage();
                    return;
                case 4:
                    CommonUI.displayExitMessage();
                    System.exit(0);
            }
        }
    }

    // 선택 및 장바구니 담기
    public void PlaceOnOrdersInsertBag(int i) {
        boolean running = true;

        while (running) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("===================================");
                displayAllOrderableStocks(0);

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
            if (i == 1) {
                    System.out.println("1.물품 추가\t2.상위메뉴\n ");
                    System.out.print(">> ");
                    int x = sc.nextInt();

                    if (x == 2) {
                        CommonUI.displayGoBackMessage();
                        return;
                    }
            }
        }
        PlaceOnOrderschoice();

    }


    // 발주 내역 조회
    public void displayAllHistoryOrderableStocks(int i) {
        boolean running = true;

        if (i == 1) {
            while (running) {
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
                System.out.println("1.상위 메뉴\t2. 종료");
                int a = sc.nextInt();
                switch (a) {
                    case 1:
                        CommonUI.displayGoBackMessage();
                        return;
                    case 2:
                        CommonUI.displayExitMessage();
                        System.exit(0);
                    default:
                        CommonUI.displayWrongSelectMessage();
                }
            }
        } else {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 발주내역 카테고리 조회
    public void displayHistoryOrderableStocksByCategory() {
        boolean running = true;

        while (running) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("===================================\n");
                System.out.println("[카테고리 조회] \n");
                System.out.println("0. 디저트\t1. MD\t2. 일회용품\t3. 원자재\t4. 병음료\t5. 원두\n" +
                        "\t6. 상위 메뉴로\t7. 종료");
                System.out.print(">> ");
                int choice = sc.nextInt();
                switch(choice) {
                    case 0,1,2,3,4,5:
                        ArrayList<PlaceOnOrdersHistoryDTO> stocks = dao.HistoryplaceOrdersStockByCategory(con, choice);
                        if (stocks.isEmpty()) {
                            System.out.println("발주 내역이 없습니다.");
                        } else {
                            for (PlaceOnOrdersHistoryDTO stock : stocks) {
                                System.out.println(stock);
                            }
                        }
                    case 6:
                        CommonUI.displayGoBackMessage();
                        return; // 상위 메뉴로
                    case 7:
                        CommonUI.displayExitMessage();
                        System.exit(0);// 종료
                    default:
                        CommonUI.displayWrongSelectMessage();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //발주 수정 및 취소
    public void UpdateDeleteOrderableStock() {
        boolean running = true;

        while (running) {
                System.out.println("===================================");
                System.out.println("[발주 수정 및 취소] \n");
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
                        CommonUI.displayGoBackMessage();
                        return;
                    case 4:
                        CommonUI.displayExitMessage();
                        System.exit(0);
                        break;
                    default:
                        CommonUI.displayWrongSelectMessage();
                }
        }
    }
    // 발주 취소 구현
    public void DeleteOrderableStock() {
        boolean running = true;

        while (running) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("===================================");
                System.out.print(">> ");
                displayAllHistoryOrderableStocks(0);


                System.out.println("삭제하실 발주번호를 입력해주세요: ");
                System.out.print(">> ");

                int stNo = sc.nextInt();

                System.out.println("정말 삭제하시겠습니까? (Y/N): ");
                System.out.print(">> ");

                String a = sc.next();

                if (a.equals("Y")) {
                    PlaceOrdersStockVO vo2 = new PlaceOrdersStockVO();
                    PlaceOrdersVO vo1 = new PlaceOrdersVO();

                    vo2.setPoNo(stNo);
                    vo1.setPoNo(stNo);
                    dao.DeleteOrderHistory2(con, vo2);
                    dao.DeleteOrderHistory(con, vo1);

                    System.out.println("품목이 삭제되었습니다.");
                } else {
                    System.out.println("취소되었습니다.");
                }

            } catch (SQLException e) {
                System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());

            }
            System.out.println("1.상위 메뉴\t2. 종료");
            int a = sc.nextInt();
            switch (a) {
                case 1:
                    CommonUI.displayGoBackMessage();
                    return;
                case 2:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }
        }
    }

    // 발주 수정 구현
    public void UpdateOrderableStock() {
        boolean running = true;

        while (running) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("===================================");
                displayAllHistoryOrderableStocks(0);


                System.out.println("수정하실 품목의 고유번호(St_no)를 입력해주세요: ");
                System.out.print(">> ");
                int stNo = sc.nextInt();

                System.out.println("수정하실 발주번호를(Po_no) 입력해주세요");
                System.out.print(">> ");
                int poNo = sc.nextInt();

                System.out.println("변경하실 수량을 입력해주세요: ");
                System.out.print(">> ");
                int postQuantity = sc.nextInt();

                PlaceOrdersStockVO vo = new PlaceOrdersStockVO();
                vo.setStNo(stNo);
                vo.setPostQuantity(postQuantity);
                vo.setPoNo(poNo);
                int result = dao.UpdateOrderHistory(con, vo);
                if (result == 1) {
                    System.out.println("품목이 수정되었습니다.");
                } else {
                    System.out.println("수정 과정에서 오류가 발생하였습니다.");
                }
                System.out.println("1.상위 메뉴\t2. 종료");
                int a = sc.nextInt();
                switch (a) {
                    case 1:
                        CommonUI.displayGoBackMessage();
                        return;
                    case 2:
                        CommonUI.displayExitMessage();
                        System.exit(0);
                    default:
                        CommonUI.displayWrongSelectMessage();


                }
            }catch (SQLException e) {
                System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());

            }
        }

        }


    /**
     * 기간별 주문 내역 목록 출력
     */
    private void PlaceOnOrdersHistoryByPeriod(){
        try(Connection con = dataSource.getConnection()) {
            boolean running = true;

            while (running) {
                System.out.println("===================================\n");
                System.out.println("[기간별 발주 내역 조회]\n");

                System.out.println("1. 연도별\t2. 월별\t3. 일자별\t4. 상위 메뉴\t5. 종료\n");
                System.out.print(">> ");
                int choice = sc.nextInt();

                int year = 0;
                int month = 0;
                int day = 0;

                switch (choice) {
                    case 3:
                        System.out.println("\n조회할 일을 입력해주세요(ex. 28)\n");
                        System.out.print(">> ");
                        day = sc.nextInt();
                    case 2:
                        System.out.println("\n조회할 월을 입력해주세요(ex. 2)\n");
                        System.out.print(">> ");
                        month = sc.nextInt();
                    case 1:
                        System.out.println("\n조회할 연도를 입력해주세요(ex. 2024)\n");
                        System.out.print(">> ");
                        year = sc.nextInt();
                        break;
                    case 4:
                        CommonUI.displayGoBackMessage();
                        return;
                    case 5:
                        CommonUI.displayExitMessage();
                        System.exit(0);
                    default:
                        CommonUI.displayWrongSelectMessage();
                }

                if (choice == 1) {
                    ArrayList<PlaceonOrdersCheckDTO> stocks = dao.PlaceOnOrdersHistoryByYear(con, year);
                    if (stocks.isEmpty()) {
                        System.out.println("발주 내역이 없습니다.");
                    } else {
                        for (PlaceonOrdersCheckDTO stock : stocks) {
                            System.out.println(stock);
                        }
                    }
                }
                else if(choice == 2) {
                    ArrayList<PlaceonOrdersCheckDTO> stocks = dao.PlaceOnOrdersHistoryByMonth(con, month, year);
                    if (stocks.isEmpty()) {
                        System.out.println("발주 내역이 없습니다.");
                    } else {
                        for (PlaceonOrdersCheckDTO stock : stocks) {
                            System.out.println(stock);
                        }
                    }
                }
                else{
                    ArrayList<PlaceonOrdersCheckDTO> stocks = dao.PlaceOnOrdersHistoryByDay(con, day, month,year);
                    if (stocks.isEmpty()) {
                        System.out.println("발주 내역이 없습니다.");
                    } else {
                        for (PlaceonOrdersCheckDTO stock : stocks) {
                            System.out.println(stock);
                        }
                    }
                }
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }


        }


    }


















