package com.ssginc.manageproduct.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.manageproduct.dao.ManageProductDAO;
import com.ssginc.manageproduct.vo.ManageProductVO;

import java.util.ArrayList;
import java.util.Scanner;

public class ManageProductUI {
    private final ManageProductDAO manageProductDAO;
    Scanner sc;

    public ManageProductUI(Scanner sc) throws Exception {
        this.sc = sc;
        manageProductDAO = new ManageProductDAO();
    }

    // 메뉴 선택 화면
    public void selectManageProductMenu() throws Exception {
        while (true) {
            System.out.println("===================================");
            System.out.println("[메뉴 선택]");
            System.out.println("1. 품목 조회\t2. 품목 추가\t3. 종료\n");
            System.out.print(">> ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    selectProductListMenu();
                    break;
                case 2:
                    insertProduct();
                    break;
                case 3:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                    break;
                default:
                    CommonUI.displayWrongSelectMessage();
                    break;
            }
        }
    }

    // 품목 조회 메뉴 화면
    private void selectProductListMenu() throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("===================================");
            System.out.println("[품목 조회]");
            System.out.println("1. 모든 품목 조회\t2. 특정 품목 조회(카테고리)\t3. 특정 품목 조회(키워드)\t4. 상위 메뉴\t5.종료\n");
            System.out.print(">> ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    selectAllProducts();
                    break;
                case 2:
                    selectProductListByCategory();
                    break;
                case 3:
                    selectProductListByKeyword();
                    break;
                case 4:
                    return;
                case 5:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                    break;
                default:
                    CommonUI.displayWrongSelectMessage();
                    break;
            }
        }
    }

    // 품목 선택과 품목 추가를 선택하는 메뉴 화면
    private void selectMenuAfterPrintProductList() throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("===================================");
            System.out.println("[품목 수정 및 선택 및 추가]");
            System.out.println("1. 특정 품목 수정 및 삭제 \t2. 품목 추가\t3. 상위메뉴\t4. 종료\n");
            System.out.print(">> ");

            int choice = sc.nextInt();
            sc.nextLine();


            switch (choice) {
                case 1:
                    updateOrDeleteProduct();
                    break;
                case 2:
                    insertProduct();
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
                    break;
            }
        }
    }

    private void updateOrDeleteProduct() throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("===================================");
            System.out.println("[품목 수정 및 삭제]");
            System.out.println("1. 특정 품목 수정\t2. 특정 품목 삭제\t3. 상위메뉴\t4. 종료\n");
            System.out.print(">> ");
            int choice = sc.nextInt();

            ManageProductDAO dao = new ManageProductDAO();

            switch (choice) {
                case 1:
                    System.out.println("수정하실 품목의 번호를 입력해주세요.");
                    System.out.print(">> ");
                    int st_no = sc.nextInt();
                    System.out.print("품목의 이름을 입력해주세요.");
                    System.out.print(">> ");
                    String st_name = sc.next();
                    sc.nextLine();

                    if (dao.update(st_name, st_no)) {
                        System.out.println("품목이 성공적으로 수정되었습니다!");
                    } else {
                        System.out.println("수정 실패: 해당 번호의 품목이 존재하지 않습니다.");
                    }
                    break;
                case 2:
                    System.out.println("삭제하실 제품의 번호를 입력해주세요");
                    System.out.print(">> ");
                    st_no = sc.nextInt();
                    if (dao.delete(st_no)) {
                        System.out.println("품목이 삭제되었습니다");
                    } else {
                        System.out.println("삭제 실패: 해당 번호의 품목이 존재하지 않습니다.");
                    }
                    break;
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도하세요.");
                    break;
            }

            System.out.println("1.상위 메뉴\t2. 종료");
            System.out.print(">> ");
            int i = sc.nextInt();
            sc.nextLine();
            switch (i) {
                case 1:
                    CommonUI.displayGoBackMessage();
                    return;
                case 2:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
                    break;
            }
        }
    }

    public void selectAllProducts() throws Exception {
        ManageProductDAO dao = new ManageProductDAO();
        ArrayList<ManageProductVO> list = dao.selectAll();

        for (ManageProductVO vo : list) {
            System.out.println(vo);
        }
        selectMenuAfterPrintProductList();
    }

    private void selectProductListByCategory() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("===================================");
        System.out.println("[카테고리 별 조회]");
        System.out.println(" 0. 디저트\t1. MD\t2. 일회용품");
        System.out.println(" 3. 원자재\t4. 병음료\t5. 원두\n");
        System.out.print(">> ");

        int st_category = sc.nextInt();
        sc.nextLine();

        ManageProductDAO dao = new ManageProductDAO();
        ArrayList<ManageProductVO> list = dao.selectCategory(st_category);

        if (list.size() == 0) System.out.println("검색 결과 없음");
        for (ManageProductVO vo : list) {
            System.out.println(vo);
        }
        selectMenuAfterPrintProductList();
    }

    private void selectProductListByKeyword() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("===================================");
        System.out.println("[키워드 별 조회]");
        System.out.println("검색하실 키워드를 입력해주세요.");
        System.out.print(">> ");
        String st_name = sc.next();
        sc.nextLine();

        ManageProductDAO dao = new ManageProductDAO();
        ArrayList<ManageProductVO> list = dao.selectKeyword(st_name);

        for (ManageProductVO vo : list) {
            System.out.println(vo);
        }
        selectMenuAfterPrintProductList();
    }

    private void insertProduct() throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("===================================");
            System.out.println("[품목 추가]");

            System.out.println("추가할 품목의 정보를 입력하세요.");
            System.out.print(">> ");
            String st_name = sc.nextLine();

            System.out.print("가격을 입력하세요. ");
            System.out.print(">> ");
            int st_price = sc.nextInt();
            sc.nextLine();

            System.out.print("카테고리를 입력하세요. ");
            System.out.print(">> ");

            int st_category = sc.nextInt();

            System.out.print("단위를 입력하세요: (g, mg, EA): ");
            System.out.print(">> ");
            String st_unit = sc.next();

            ManageProductVO newProduct = new ManageProductVO(st_name, st_price, 0, 0, st_category, st_unit, 1);
            ManageProductDAO dao = new ManageProductDAO();

            int result = dao.insert(newProduct);
            if (result == 1) {
                System.out.println("품목이 성공적으로 추가되었습니다.");
            } else {
                System.out.println("품목 추가 중 오류가 발생했습니다: ");
            }
            System.out.println("1.상위 메뉴\t2. 종료");
            System.out.print(">> ");
            int i = sc.nextInt();
            sc.nextLine();
            switch (i) {
                case 1:
                    CommonUI.displayGoBackMessage();
                    return;
                case 2:
                    CommonUI.displayExitMessage();
                    System.exit(0);
                default:
                    CommonUI.displayWrongSelectMessage();
                    break;
            }
        }
    }
}