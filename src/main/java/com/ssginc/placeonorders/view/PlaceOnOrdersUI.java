package com.ssginc.placeonorders.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.placeonorders.model.dao.PlaceOnOrdersDAO;
import com.ssginc.placeonorders.model.dto.*;
import com.ssginc.placeonorders.model.vo.PlaceOrdersStockVO;
import com.ssginc.placeonorders.model.vo.PlaceOrdersVO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlaceOnOrdersUI {

    private final Scanner sc;
    private final PlaceOnOrdersDAO placeOnOrdersDAO;
    private final UsersDTO user;
    private final DataSource dataSource;
    String[] category = {"디저트", "MD", "일회용품", "원자재", "병음료", "원두"};

    public PlaceOnOrdersUI(UsersDTO user, Scanner sc) {
        this.sc = sc;
        placeOnOrdersDAO = new PlaceOnOrdersDAO();
        this.user = user;
        this.dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 발주 메뉴 선택 메서드
    public void placeOnOrderschoice() {
        while (true) {
            int choice = placeOnOrders();

            switch (choice) {
                case 1:
                    this.selectStockList();             // 재고 조회 로직
                    break;
                case 2:
                    this.registerPlaceOnOrdersMenu();   // 발주 신청 로직
                    break;
                case 3:
                    this.displayHistoryStocks();        // 발주 내역 조회 로직
                    break;
                case 4:
                    this.displayOrderableStocks();      // 발주 가능 품목 조회 로직
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

    // 발주 메뉴 출력 메서드
    private int placeOnOrders() {
        System.out.println("==========================================================================================================");
        System.out.println("[발주]");
        System.out.println("1. 재고 조회\t2. 발주 신청\t3. 발주 내역 조회\t4. 발주 가능 품목 조회\t5. 상위 메뉴\t6. 종료");
        System.out.print(">> ");
        return sc.nextInt();
    }

    // ========================== 1. 재고 조회 ==========================
    // 재고 조회 메뉴
    private void selectStockList() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[재고 조회]");
            System.out.println("1. 전체 조회\t2. 카테고리 조회\t3. 키워드 검색\t4. 상위 메뉴\t5. 종료");
            System.out.print(">> ");
            int choice = sc.nextInt();

            List<SelectStockListDTO> result = null;
            String title = null;
            switch (choice) {
                // 전체 조회
                case 1:
                    result = placeOnOrdersDAO.selectAllStockList();

                    if (result == null) {
                        // CommonUI의 메서드로 빼는 것도 좋을 거 같다고 생각
                        System.out.println("조회된 결과가 없습니다.");
                        break;
                    }

                    title = "[재고 전체 조회]";
                    this.printStockList(result, title);
                    break;
                // 카테고리 조회
                case 2:
                    int categoryNum = selectCategory();
                    result = placeOnOrdersDAO.selectStockListByCategory(categoryNum);

                    if (result == null) {
                        // CommonUI의 메서드로 빼는 것도 좋을 거 같다고 생각
                        System.out.println("조회된 결과가 없습니다.");
                        break;
                    }

                    title = "[재고 카테고리별 조회]";
                    this.printStockList(result, title);
                    break;
                // 키워드 검색
                case 3:
                    String searchKeyword = inputKeyword();
                    result = placeOnOrdersDAO.selectStockListByKeyword(searchKeyword);

                    if (result == null) {
                        // CommonUI의 메서드로 빼는 것도 좋을 거 같다고 생각
                        System.out.println("조회된 결과가 없습니다.");
                        break;
                    }

                    title = "[재고 키워드 검색]";
                    this.printStockList(result, title);
                    break;
                // 상위 메뉴
                case 4:
                    CommonUI.displayGoBackMessage();
                    return;
                // 종료
                case 5:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }
        }
    }

    // 재고 리스트 출력 메서드
    private void printStockList(List<SelectStockListDTO> stockList, String title) {
        System.out.println("==========================================================================================================");
        System.out.println(title);
        System.out.printf("%-8s%-20s%-15s%-15s%-10s\n", "제품번호", "제품명", "재고수량", "제품 카테고리", "제품 단위");
        System.out.println("----------------------------------------------------------------------");

        for (SelectStockListDTO stock : stockList) {
            System.out.printf("%-10s%-20s%-15s%-15s%-10s\n",
                    stock.getStNo(),
                    stock.getStName(),
                    stock.getStQuantity(),
                    category[stock.getStCategory()],
                    stock.getStUnit());
        }
    }

    // 카테고리 입력 메서드
    private int selectCategory() {
        System.out.println("==========================================================================================================");
        for (int i = 0; i < category.length; i++) {
            System.out.print((i + 1) + ". " + category[i] + "\t\t");
        }
        System.out.println();
        System.out.print(">> ");

        return sc.nextInt();
    }

    // 검색 키워드 입력 메서드
    private String inputKeyword() {
        sc.nextLine();  // 입력 버퍼 비워주기
        System.out.println("==========================================================================================================");
        System.out.println("검색할 단어를 입력하세요.");
        System.out.print(">> ");

        return sc.nextLine();
    }

    // ========================== 2. 발주 신청 ==========================
    // 발주 신청 메뉴
    private void registerPlaceOnOrdersMenu() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 신청]");
            System.out.println();

            if (this.user == null) {
                System.out.println("회원 정보가 잘못되었습니다.");
                return;
            }
            // 로그인한 유저가 장바구니에 담은 품목 리스트 출력
            List<SelectBasketListDTO> basketList = placeOnOrdersDAO.selectBasketListByUsersNo(this.user.getUsersNo());
            // 장바구니에 담긴 품목의 총 가격
            int totalPrice = this.calculateTotalPrice(basketList);
            // 선택된 품목이 품목 리스트에 존재하는지 확인하기 위해 품목 리스트 번호
            List<Integer> basketStockNoList = this.printBasketList(basketList, totalPrice);

            System.out.println("==========================================================================================================");
            System.out.println("1. 품목 선택\t2. 발주 신청\t3. 상위 메뉴\t4. 종료");
            System.out.print(">> ");
            int choice = sc.nextInt();

            switch (choice) {
                // 품목 선택
                case 1 -> {
                    // 장바구니 품목 리스트에서 품목 선택
                    int selectedBasketStockNo = selectBasketStock(basketStockNoList);
                    // 선택된 장바구니 품목으로 DB에서 원하는 정보를 가져와 출력
                    SelectBasketListDTO selectedBasketStock = placeOnOrdersDAO.selectBasketStockByUsersNoAndStockNo(this.user.getUsersNo(), selectedBasketStockNo);
                    if (selectedBasketStock == null) {
                        System.out.println("잘못된 입력입니다.");
                        CommonUI.displayGoBackMessage();
                        break;
                    }
                    // 선택한 품목에 대한 정보를 출력
                    printSelectedBasketStock(selectedBasketStock);
                    // 선택된 품목에 대해 수정 또는 삭제
                    updateOrDeleteSelectedBasketStock(selectedBasketStock);
                }
                // 발주 신청
                case 2 -> registerPlaceOnOrders(basketList, totalPrice);
                // 상위 메뉴
                case 3 -> {
                    CommonUI.displayGoBackMessage();
                    return;
                }
                // 종료
                case 4 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
                default -> CommonUI.displayWrongSelectMessage();
            }
        }
    }

    // -------------------------- 2.1 장바구니 품목 선택 --------------------------
    // 품목 선택 메서드
    private int selectBasketStock(List<Integer> basketStockNoList) {
        System.out.println("==========================================================================================================");
        System.out.println("[품목 선택]");
        System.out.println("선택할 품목의 제품번호를 입력하세요.");
        System.out.print(">> ");

        while (true) {
            int inputBasketStockNo = sc.nextInt();
            if (basketStockNoList.contains(inputBasketStockNo)) {
                return inputBasketStockNo;
            } else {
                System.out.println("목록에 없는 제품 번호입니다. 다시 입력해주세요.");
                System.out.print(">> ");
            }
        }
    }

    // -------------------------- 2.1 장바구니 품목 수정 및 삭제 메뉴 --------------------------
    // 선택된 품목에 대해 수정 또는 삭제 메서드
    private void updateOrDeleteSelectedBasketStock(SelectBasketListDTO selectedBasketStock) {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[선택된 품목 수정, 삭제]");
            System.out.println("1. 장바구니 품목 수정\t2. 장바구니 품목 삭제\t3. 상위 메뉴\t4. 종료");
            System.out.print(">> ");
            int choice = sc.nextInt();

            switch (choice) {
                // 장바구니 품목 수정
                case 1 -> {
                    updateSelectedBasketStock(selectedBasketStock);
                    CommonUI.displayGoBackMessage();
                    return;
                }
                // 장바구니 품목 삭제
                case 2 -> {
                    deleteSelectedBasketStock(selectedBasketStock);
                    CommonUI.displayGoBackMessage();
                    return;
                }
                // 상위 메뉴
                case 3 -> {
                    CommonUI.displayGoBackMessage();
                    return;
                }
                // 종료
                case 4 -> {
                    CommonUI.displayExitMessage();
                    System.exit(0);
                }
            }
        }
    }

    // -------------------------- 2.1.1 장바구니 품목 수정 --------------------------
    // 장바구니 품목 수정 메서드
    private void updateSelectedBasketStock(SelectBasketListDTO selectedBasketStock) {
        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);   // Auto Commit 비활성화

            System.out.println("==========================================================================================================");
            System.out.println("[선택된 품목 수정]");
            System.out.println("선택된 " + selectedBasketStock.getStName() + "의 변경할 수량을 입력하세요.");
            System.out.print(">> ");

            int inputQuantity = sc.nextInt();

            int result = placeOnOrdersDAO.updateBasketStock(con, this.user.getUsersNo(), selectedBasketStock.getStNo(), inputQuantity);
            if (result == 0) {
                throw new Exception("품목의 수량을 변경하는 과정에서 오류가 발생하였습니다.");
            }

            con.commit();
            System.out.println("품목이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            try {
                con.rollback();
                System.out.println(e.getMessage());
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // -------------------------- 2.1.2 장바구니 품목 삭제 --------------------------
    // 장바구니 품목 삭제 메서드
    private void deleteSelectedBasketStock(SelectBasketListDTO selectedBasketStock) {
        System.out.println("==========================================================================================================");
        System.out.println("[선택된 품목 삭제]");
        System.out.println("선택된 " + selectedBasketStock.getStName() + "을/를 삭제하시겠습니까?");
        System.out.print("(Y/N)>> ");

        while (true) {
            String choice = sc.next();
            if (choice.equalsIgnoreCase("Y")) {
                int result = placeOnOrdersDAO.deleteBasketStockByStockNo(this.user.getUsersNo(), selectedBasketStock.getStNo());

                if (result == 1) {
                    System.out.println("품목이 성공적으로 삭제되었습니다.");
                    break;
                } else {
                    System.out.println("품목을 삭제하는 과정에서 오류가 발생하였습니다.");
                    CommonUI.displayGoBackMessage();
                    return;
                }
            } else if (choice.equalsIgnoreCase("N")) {
                CommonUI.displayGoBackMessage();
                return;
            } else {
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }

    }

    // 장바구니 리스트 출력 메서드
    private List<Integer> printBasketList(List<SelectBasketListDTO> basketList, int totalPrice) {
        List<Integer> basketStockNo = new ArrayList<>();

        System.out.printf("%-10s%-20s%-10s%-10s%-10s%-20s%-10s\n",
                "제품번호", "제품명", "단가", "발주수량", "발주가격", "제품 카테고리", "제품 단위");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        if (basketList != null) {
            for (SelectBasketListDTO basketStock : basketList) {
                int stockNo = basketStock.getStNo();

                basketStockNo.add(stockNo);

                System.out.printf("%-10s%-20s%-10d%-10d%-10d%-20s%-10s\n",
                        stockNo,
                        basketStock.getStName(),
                        basketStock.getStPrice(),
                        basketStock.getPlaceOrdersQuantity(),
                        basketStock.getPlaceOrdersPrice(),
                        category[basketStock.getStCategory()],
                        basketStock.getStUnit());
            }
        }

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Total | \t" + totalPrice);

        return basketStockNo;
    }

    // 선택한 품목에 대한 정보 출력 메서드
    private void printSelectedBasketStock(SelectBasketListDTO selectedBasketStock) {
        System.out.println("==========================================================================================================");
        System.out.printf("%-10s%-20s%-10s%-10s%-10s%-20s%-10s\n",
                "제품번호", "제품명", "단가", "발주수량", "발주가격", "제품 카테고리", "제품 단위");
        System.out.println("----------------------------------------------------------------------");

        System.out.printf("%-10s%-20s%-10d%-10d%-10d%-20s%-10s\n",
                selectedBasketStock.getStNo(),
                selectedBasketStock.getStName(),
                selectedBasketStock.getStPrice(),
                selectedBasketStock.getPlaceOrdersQuantity(),
                selectedBasketStock.getPlaceOrdersPrice(),
                category[selectedBasketStock.getStCategory()],
                selectedBasketStock.getStUnit());
        System.out.println("----------------------------------------------------------------------");
    }

    // 로그인한 유저의 장바구니 목록에 있는 품목의 총 가격 계산 메서드
    private int calculateTotalPrice(List<SelectBasketListDTO> basketList) {
        int totalPrice = 0;

        if (basketList != null) {
            for (SelectBasketListDTO basketStock : basketList) {
                totalPrice += basketStock.getPlaceOrdersPrice();
            }
        }

        return totalPrice;
    }

    // -------------------------- 2.2 장바구니 품목 발주 신청 --------------------------
    // 발주 신청
    private void registerPlaceOnOrders(List<SelectBasketListDTO> basketList, int totalPrice) {
        Connection con = null;

        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);   // Auto Commit 비활성화

            System.out.println("==========================================================================================================");
            System.out.println("[발주 신청]");
            System.out.println("발주 신청을 하시겠습니까?");
            System.out.print("(Y/N)>> ");

            while (true) {
                String choice = sc.next();
                if (choice.equalsIgnoreCase("Y")) {
                    InsertPlaceOrdersDTO dto = placeOnOrdersDAO.insertPlaceOrders(con, totalPrice, this.user.getUsersNo());

                    // insertPlaceOrders의 결과값
                    int insertPlaceOrdersResult = dto.getResult();
                    // 발주 테이블 추가된 데이터 값의 poNo를 받아온다.
                    int generatedPoNo = dto.getPoNo();

                    // 발주 테이블 데이터 삽입 실패시 Exception throw
                    if (insertPlaceOrdersResult != 1) {
                        throw new Exception("발주 테이블 데이터 삽입에 실패했습니다.");
                    }

                    // 장바구니 목록에 있는 데이터의 개수만큼 나와야 함
                    int insertPlaceOrdersStockResult = 0;
                    for (SelectBasketListDTO basketStock : basketList) {
                        insertPlaceOrdersStockResult += placeOnOrdersDAO.insertPlaceOrdersStock(con, generatedPoNo, basketStock.getStNo(), basketStock.getPlaceOrdersQuantity());
                    }

                    // 발주 물품 테이블 데이터 삽입 실패시 Exception throw
                    if (insertPlaceOrdersStockResult != basketList.size()) {
                        throw new Exception("발주 물품 테이블 데이터 삽입에 실패했습니다.");
                    }

                    con.commit();

                    int deletePlaceOrdersBasketResult = placeOnOrdersDAO.deletePlaceOrdersBasketByUsersNo(con, this.user.getUsersNo());
                    if (deletePlaceOrdersBasketResult != basketList.size()) {
                        throw new Exception("발주 장바구니 테이블 데이터 삭제에 실패했습니다.");
                    }

                    con.commit();

                    System.out.println("발주가 성공적으로 신청되었습니다.");
                    break;
                } else if (choice.equalsIgnoreCase("N")) {
                    CommonUI.displayGoBackMessage();
                    return;
                } else {
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                }
            }
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ========================== 3. 발주 내역 조회 ==========================
    // 발주 내역 조회 메뉴
    public void displayHistoryStocks() {
        while (true) {
            System.out.println("==========================================================================================================");
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

    // -------------------------- 3.1 발주 내역 전체 조회 --------------------------
    // 발주 내역 전체 조회
    public void displayAllHistoryOrderableStocks() {
        while (true) {
            try (Connection con = dataSource.getConnection()) {
                ArrayList<PlaceonOrdersCheckDTO> stocks = placeOnOrdersDAO.selectAllOrderableStockChecks(con);

                if (stocks.isEmpty()) {
                    System.out.println("발주 내역이 없습니다.");
                    return;
                } else {
                    System.out.println("[발주 내역 목록]");
                    printHistory1(stocks);
                    UpdateDeleteOrderableStockMenu();
                    return;
                }

            } catch (Exception e) {
                System.out.println("알 수 없는 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // -------------------------- 3.2 발주 내역 조건부 조회 --------------------------
    // 조건부 조회
    public void displayHistoryOrderableStocksByCondition() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[조건부 조회]");
            System.out.println("1. 기간별 조회\t2. 품목별 조회\t3. 상위 메뉴\t4. 종료");
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

    // -------------------------- 3.2.1 발주 내역 기간별 조회 --------------------------
    /**
     * 기간별 주문 내역 목록 출력
     */
    private void PlaceOnOrdersHistoryByPeriod() {
        while (true) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("==========================================================================================================");
                System.out.println("[기간별 발주 내역 조회]");
                System.out.println("1. 연도별\t2. 월별\t3. 일자별\t4. 상위 메뉴\t5. 종료");
                System.out.print(">> ");
                int choice = sc.nextInt();

                int year = 0;
                int month = 0;
                int day = 0;

                switch (choice) {
                    case 3:
                        System.out.println("\n조회할 일을 입력해주세요(ex. 28)");
                        System.out.print(">> ");
                        day = sc.nextInt();
                    case 2:
                        System.out.println("\n조회할 월을 입력해주세요(ex. 2)");
                        System.out.print(">> ");
                        month = sc.nextInt();
                    case 1:
                        System.out.println("\n조회할 연도를 입력해주세요(ex. 2024)");
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
                    ArrayList<PlaceonOrdersCheckDTO> stocks = placeOnOrdersDAO.PlaceOnOrdersHistoryByYear(con, year);
                    if (stocks.isEmpty()) {
                        System.out.println("발주 내역이 없습니다.");
                    } else {
                        System.out.println("[발주 내역 목록]");
                        printHistory1(stocks);
                        UpdateDeleteOrderableStockMenu();
                        return;
                    }
                } else if (choice == 2) {
                    ArrayList<PlaceonOrdersCheckDTO> stocks = placeOnOrdersDAO.PlaceOnOrdersHistoryByMonth(con, month, year);
                    if (stocks.isEmpty()) {
                        System.out.println("발주 내역이 없습니다.");
                    } else {
                        System.out.println("[발주 내역 목록]");
                        printHistory1(stocks);
                        UpdateDeleteOrderableStockMenu();
                        return;
                    }
                } else {
                    ArrayList<PlaceonOrdersCheckDTO> stocks = placeOnOrdersDAO.PlaceOnOrdersHistoryByDay(con, day, month, year);
                    if (stocks.isEmpty()) {
                        System.out.println("발주 내역이 없습니다.");
                    } else {
                        System.out.println("[발주 내역 목록]");
                        printHistory1(stocks);
                        UpdateDeleteOrderableStockMenu();
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------------- 3.2.2 발주 내역 카테고리별 조회 --------------------------
    // 발주내역 카테고리 조회
    public void displayHistoryOrderableStocksByCategory() {
        while (true) {
            try (Connection con = dataSource.getConnection()) {
                System.out.println("==========================================================================================================");
                System.out.println("[발주 내역 카테고리 조회]");
                System.out.println("0. 디저트\t1. MD\t2. 일회용품\t3. 원자재\t4. 병음료\t5. 원두\t6. 상위 메뉴\t7. 종료");
                System.out.print(">> ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 0, 1, 2, 3, 4, 5:
                        ArrayList<PlaceOnOrdersHistoryDTO> stocks = placeOnOrdersDAO.HistoryplaceOrdersStockByCategory(con, choice);
                        if (stocks.isEmpty()) {
                            System.out.println("발주 내역이 없습니다.");
                        } else {
                            System.out.println("[발주 내역 목록]");
                            printHistory2(stocks);
                            for (PlaceOnOrdersHistoryDTO stock : stocks) {
                                System.out.println(stock);
                            }
                            UpdateDeleteOrderableStockMenu();
                            return;
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

    // 발주 내역 전체, 기간별 조회 출력
    private void printHistory1(List<PlaceonOrdersCheckDTO> list) {
        System.out.println("==========================================================================================================");
        System.out.printf("%-8s%-8s%-20s%-8s%-8s%-15s%-15s%-10s%-20s\n", "발주번호", "제품번호", "제품명", "제품가격", "발주수량", "소계", "제품 카테고리", "사용자명", "발주일시");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (PlaceonOrdersCheckDTO stock : list) {
            System.out.printf("%-8s%-8s%-20s%-8s%-8s%-15s%-15s%-10s%-20s\n",
                    stock.getPoNo(),
                    stock.getStNo(),
                    stock.getStName(),
                    stock.getStPrice(),
                    stock.getPostQuantity(),
                    stock.getSubTotal(),
                    category[stock.getStCategory()],
                    stock.getUsersName(),
                    stock.getPoDate());
        }
    }

    // 발주 내역 카테고리별 조회
    private void printHistory2(List<PlaceOnOrdersHistoryDTO> list) {
        System.out.println("==========================================================================================================");
        System.out.printf("%-8s%-8s%-20s%-8s%-8s%-15s%-15s\n", "발주번호", "제품번호", "제품명", "제품가격", "발주수량", "소계", "제품 카테고리");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (PlaceOnOrdersHistoryDTO stock : list) {
            System.out.printf("%-8s%-8s%-20s%-8s%-8s%-15s%-15s\n",
                    stock.getPoNo(),
                    stock.getStNo(),
                    stock.getStName(),
                    stock.getStPrice(),
                    stock.getPostQuantity(),
                    stock.getSubTotal(),
                    category[stock.getStCategory()]);
        }
    }

    // -------------------------- 3.1.1 발주 수정 및 취소 --------------------------
    // 발주 수정 및 취소 메뉴
    public void UpdateDeleteOrderableStockMenu() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("1. 발주 수정 및 취소\t2. 상위 메뉴\t3. 종료");
            System.out.print(">> ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    UpdateDeleteOrderableStock();
                    break;
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
    }

    // 발주 수정 및 취소
    public void UpdateDeleteOrderableStock() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 수정 및 취소]");
            System.out.println("1. 발주 수정\t2. 발주 취소\t3. 상위 메뉴\t4. 종료");
            System.out.print(">> ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    updateOrderableStock();
                    break;
                case 2:
                    deleteOrderableStock();
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

    // -------------------------- 3.1.1.1 발주 수정 --------------------------
    // 발주 수정 구현
    public void updateOrderableStock() {
        try (Connection con = dataSource.getConnection()) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 수정]");

            System.out.println("수정하실 발주번호를(Po_no) 입력해주세요");
            System.out.print(">> ");
            int poNo = sc.nextInt();

            System.out.println("수정하실 품목의 고유번호(St_no)를 입력해주세요: ");
            System.out.print(">> ");
            int stNo = sc.nextInt();

            System.out.println("변경하실 수량을 입력해주세요: ");
            System.out.print(">> ");
            int postQuantity = sc.nextInt();

            PlaceOrdersStockVO vo = new PlaceOrdersStockVO();
            vo.setStNo(stNo);
            vo.setPostQuantity(postQuantity);
            vo.setPoNo(poNo);
            int result = placeOnOrdersDAO.UpdateOrderHistory(con, vo);
            if (result == 1) {
                System.out.println("품목이 수정되었습니다.");
            } else {
                System.out.println("수정 과정에서 오류가 발생하였습니다.");
            }
        } catch (SQLException e) {
            System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());
        }

    }

    // -------------------------- 3.1.1.2 발주 취소 --------------------------
    // 발주 취소 구현
    public void deleteOrderableStock() {
        try (Connection con = dataSource.getConnection()) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 취소]");

            System.out.println("삭제하실 발주번호를 입력해주세요: ");
            System.out.print(">> ");
            int stNo = sc.nextInt();

            System.out.println("정말 삭제하시겠습니까? (Y/N): ");
            System.out.print(">> ");
            String a = sc.next();

            if (a.equalsIgnoreCase("Y")) {
                PlaceOrdersStockVO vo2 = new PlaceOrdersStockVO();
                PlaceOrdersVO vo1 = new PlaceOrdersVO();

                vo2.setPoNo(stNo);
                vo1.setPoNo(stNo);
                placeOnOrdersDAO.DeleteOrderHistory2(con, vo2);
                placeOnOrdersDAO.DeleteOrderHistory(con, vo1);

                System.out.println("품목이 삭제되었습니다.");
            } else {
                System.out.println("취소되었습니다.");
            }

        } catch (SQLException e) {
            System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());

        }
    }

    // ========================== 4. 발주 가능 품목 조회 ==========================
    // -------------------------- 4. 발주 가능 품목 조회 --------------------------
    //  발주 가능 품목 조회
    public void displayOrderableStocks() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 가능 품목 조회]");
            System.out.println("1. 전체 조회\t2. 카테고리 조회\t3. 검색\t4. 상위 메뉴\t5. 종료");
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
                    CommonUI.displayGoBackMessage();
                    return;
                case 5:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
            }
        }
    }

    // -------------------------- 4.1 발주 가능 품목 전체 조회 --------------------------
    // 전체 발주 가능 품목 조회 구현
    public void displayAllOrderableStocks() {
        while (true) {
            try (Connection con = dataSource.getConnection()) {
                ArrayList<PlaceonOrdersDTO> stocks = placeOnOrdersDAO.selectAllOrderableStocks(con);

                if (stocks.isEmpty()) {
                    System.out.println("발주 가능한 품목이 없습니다.");
                    return;
                } else {
                    System.out.println("[발주 가능 품목 목록]");
                    printHistory3(stocks);
                    placeOnOrdersInsertBasketMenu();
                    return;
                }
            } catch (SQLException e) {
                System.out.println("발주 가능 품목 조회 중 오류: " + e.getMessage());
            }
        }
    }

    // -------------------------- 4.2 발주 가능 품목 카테고리별 조회 --------------------------
    // 카테고리별 조회 구현
    public void displayAllOrderableStocksbyCategory() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 가능 품목 카테고리 조회]");
            System.out.println("1. 디저트\t2. MD\t3. 일회용품\t4. 원자재\t5. 병음료\t6. 원두\n" +
                    "\t7. 상위 메뉴\t8. 종료");
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
                ArrayList<PlaceonOrdersDTO> stocks = placeOnOrdersDAO.selectAllOrderableStocksByCategory(con, a);

                if (stocks.isEmpty()) {
                    System.out.println("조회 가능한 재고가 없습니다.");
                    return;
                } else {
                    System.out.println("[발주 가능 품목 목록]");
                    printHistory3(stocks);
                    placeOnOrdersInsertBasketMenu();
                    return;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // -------------------------- 4.3 발주 가능 품목 키워드 검색 --------------------------
    // 키워드로 검색 구현
    public void displayAllOrderableStocksbyKeyword() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[발주 가능 품목 키워드 검색]");
            System.out.println("검색하실 키워드를 입력해주세요");
            System.out.print(">> ");
            String keyword = sc.next();
            try (Connection con = dataSource.getConnection()) {
                ArrayList<PlaceonOrdersDTO> stocks = placeOnOrdersDAO.selectAllOrderableStocksByKeyword(con, keyword);

                if (stocks.isEmpty()) {
                    System.out.println("키워드에 맞는 조회 가능한 재고가 없습니다.");
                    return;
                } else {
                    System.out.println("[발주 가능 품목 목록]");
                    printHistory3(stocks);
                    placeOnOrdersInsertBasketMenu();
                    return;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // 발주 가능 품목 출력
    private void printHistory3(List<PlaceonOrdersDTO> list) {
        System.out.println("==========================================================================================================");
        System.out.printf("%-8s%-20s%-15s%-15s%-10s\n", "제품번호", "제품명", "제품가격", "제품 카테고리", "제품 단위");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (PlaceonOrdersDTO stock : list) {
            System.out.printf("%-10s%-20s%-15s%-15s%-10s\n",
                    stock.getStNo(),
                    stock.getStName(),
                    stock.getStPrice(),
                    category[stock.getStCategory()],
                    stock.getStUnit());
        }
    }

    // -------------------------- 4.1.1 품목 선택 및 장바구니 담기 --------------------------
    // 품목 선택 및 장바구니 담기 메뉴
    public void placeOnOrdersInsertBasketMenu() {
        while (true) {
            System.out.println("==========================================================================================================");
            System.out.println("[품목 선택 및 장바구니 담기 메뉴]");
            System.out.println("1. 선택 및 장바구니 담기\t2. 상위 메뉴\t3. 종료");
            System.out.print(">> ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    placeOnOrdersInsertBag();
                    break;
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
    }

    // 선택 및 장바구니 담기
    public void placeOnOrdersInsertBag() {
        try (Connection con = dataSource.getConnection()) {
            System.out.println("==========================================================================================================");
            System.out.println("[품목 선택 및 장바구니 담기]");

            System.out.print("선택하실 품목의 고유번호를 입력해주세요: ");
            int stNo = sc.nextInt();

            System.out.print("수량을 입력해주세요: ");
            int pobQuantity = sc.nextInt();

            System.out.print("장바구니에 담으시겠습니까? (Y/N): ");
            String a = sc.next();

            if (a.equalsIgnoreCase("Y")) {
                PlaceOnOrdersInsertBagDTO dto = new PlaceOnOrdersInsertBagDTO();
                dto.setUsers_no(this.user.getUsersNo());
                dto.setStNo(stNo);
                dto.setPobQuantity(pobQuantity);

                placeOnOrdersDAO.placeOnOrdersInsertBag(con, dto);
                System.out.println("장바구니에 추가되었습니다.");
            } else {
                System.out.println("취소되었습니다.");
            }

        } catch (SQLException e) {
            System.out.println("장바구니 처리 중 오류 발생: " + e.getMessage());
        }
    }

}