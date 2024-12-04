package com.ssginc.manageproduct.view;

import com.ssginc.manageproduct.dao.ManageProductDAO;
import com.ssginc.manageproduct.vo.ManageProductVO;

import java.util.ArrayList;
import java.util.List;
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


        boolean flag = false;
        // 메뉴 선택 화면 출력
        while (true) {

            System.out.println("1. 품목 조회");
            System.out.println("2. 품목 추가");
            System.out.println("0. 종료");
            System.out.print("메뉴를 선택하세요>> ");

//            if (!sc.hasNextInt()) {
//                System.out.println("올바른 숫자를 입력하세요.");
//                sc.next(); // 잘못된 입력 처리
//                continue;
//            }
//            System.out.println("1----" + sc.nextLine());
            //System.out.println("2----" + sc.nextLine());
            int choice = sc.nextInt();

//            String ch = sc.next();
//            System.out.println("ch내용>>> " + ch);
//            int choice = -1;
//            if(ch.equals("")){
//                System.out.println("공백이 입력됨.");
//                continue;
//            }else{
//                choice = Integer.parseInt(ch);
//            }
//            sc.nextLine(); // 버퍼 비우기


            switch (choice) {
                case 1:
                    selectProductListMenu();
//                    sc.close();
                    break;
                case 2:
                    insertProduct();
//                    sc.close();
                    break;
                case 0:
                    System.out.println("프로그램을 종료합니다.");
//                    sc.close();
                    return;
                default:
                    System.out.println("올바른 번호를 입력하세요.");
            }


        } //while


        // 1번 선택시 품목 조회 메서드 호출      selectProductListMenu()

        // 2번 선택시 품목 추가 메서드 호출      insertProduct()

    }

    // 품목 조회 메뉴 화면
    private void selectProductListMenu() throws Exception {
        //System.out.println("--------------->>>>");
        Scanner sc = new Scanner(System.in);
        // 품목 조회 메뉴 선택 화면 출력


        System.out.println("1. 모든 품목 조회");
        System.out.println("2. 특정 품목 조회(카테고리)");
        System.out.println("3. 특정 품목 조회(키워드)");

        System.out.print("품목 조회 메뉴를 선택하세요:");
        // 메뉴 번호 입력
        int choice = sc.nextInt();
        sc.nextLine(); // 버퍼 비우기


        switch (choice) {
            case 1:
                selectAllProucts();
                break;
            case 2:
                selectProductListByCategory();
                break;
            case 3:
                selectProductListByKeyword();
                break;
            default:
                System.out.println("올바른 번호를 입력하세요.");
        }

        selectMenuAfterPrintProductList();


        // 1. 전체 조회     2. 카테고리 조회      3. 키워드 검색 조회

        // 1번 선택시 전체 조회 메서드 호출           selectAllProucts()

        // 2번 선택시 카테고리 조회 메서드 호출        selectProductListByCategory()

        // 3번 선택시 키워드 검색 조회 메서드 호출      selectProductListByKeyword()

        // 조회가 완료되면 품목 선택과 품목 추가를 하는 메서드 호출     selectMenuAfterPrintProductList()
//        sc.close();
    }

    // 품목 선택과 품목 추가를 선택하는 메뉴 화면
    private void selectMenuAfterPrintProductList() throws Exception {
        Scanner sc = new Scanner(System.in);
        // 품목 선택 또는 품목 추가 메뉴 선택 화면 출력


        System.out.println("1. 품목 선택");
        System.out.println("2. 품목 추가");
        System.out.println("3. 처음으로");
        System.out.print("메뉴를 선택하세요: ");

        int choice = sc.nextInt();
        sc.nextLine();

        // 메뉴 번호 입력
        switch (choice) {
            case 1:
                updateOrDeleteProduct();
                break;
            case 2:
                insertProduct();
                break;
            case 3:
                selectManageProductMenu();
                break;
            default:
                System.out.println("올바른 번호를 입력하세요.");
        }

//        sc.close();
        // 1. 품목 선택     2. 품목 추가
        // 1번 선택시 품목 선택 메서드 호출      updateOrDeleteProduct()
        // 2번 선택시 품목 추가 메서드 호출      insertProduct()
    }

    private void updateOrDeleteProduct() throws Exception {
        // 품목 선택 (품목 번호로 입력 받기)
        Scanner sc = new Scanner(System.in);

        // 품목 수정과 품목 삭제 중 어떤 메뉴를 선택할 것인지 출력하고 입력받기
        System.out.println("1. 특정 품목 수정");
        System.out.println("2. 특정 품목 삭제");
        System.out.print("품목 조회 메뉴를 선택하세요: ");

        int choice = sc.nextInt();
        //sc.nextLine();

        // 1. 품목 수정 DAO의 update() 호출
        ManageProductDAO dao = new ManageProductDAO();


        switch (choice) {
            case 1:
                System.out.print("수정할 이름");
                String st_name = sc.next();
                //sc.nextLine();
                System.out.print("수정할 번호");
                int st_no = sc.nextInt();
                sc.nextLine();
                if (dao.update(st_name, st_no)) {
                    System.out.println("품목이 성공적으로 수정되었습니다!");
                } else {
                    System.out.println("수정 실패: 해당 번호의 품목이 존재하지 않습니다.");
                }
                break;


            case 2:
                System.out.print("삭제할 번호");
                st_no = sc.nextInt();
                if (dao.delete(st_no)) {
                    System.out.println("품목이 삭제되었습니다!");
                } else {
                    System.out.println("삭제 실패: 해당 번호의 품목이 존재하지 않습니다.");
                }

                break;

            default:
                System.out.println("잘못된 선택입니다. 다시 시도하세요.");
                break;
        }


//        sc.close();


        // dao.update(st_name, st_no);
        ;

        // 2. 품목 삭제 DAO의 delete() 호출


        //dao.delete(st_no);
    }

    // 품목 전체 조회
    public void selectAllProucts() throws Exception {
        // 화면 출력
        // Scanner sc = new Scanner(System.in);

        // 품목 조회 메뉴 선택 화면 출력

        // DAO의 selectAll() 호출
        ManageProductDAO dao = new ManageProductDAO();
        ArrayList<ManageProductVO> list = dao.selectAll();

        // 전체 품목 리스트 출력
        for (ManageProductVO vo : list) {
            System.out.println(vo);
//            System.out.println("재고물품명: " + vo.getSt_name());
//            System.out.println("가격: " + vo.getSt_price());
//            System.out.println("재고수량: " + vo.getSt_quantity());
//            System.out.println("구분자: " + vo.getSt_owner());
//            System.out.println("카테고리: " + vo.getSt_category());
//            System.out.println("단위: " + vo.getSt_unit());
//            System.out.println("발주가능여부: " + vo.getSt_state());
//            System.out.println("------------------");
        }
        //sc.close();
    }

    // 품목 카테고리별 조회
    private void selectProductListByCategory() throws Exception {
        // 화면 출력
        Scanner sc = new Scanner(System.in);


        //어떤 카테고리가 있는지 프린트!
        System.out.println(" 0 - 디저트");
        System.out.println(" 1 - MD");
        System.out.println(" 2 - 일회용품");
        System.out.println(" 3 - 원자재");
        System.out.println(" 4 - 병음료");
        System.out.println(" 5 - 원두");

        System.out.print("조회할 카테고리를 입력하세요: ");
        // 카테고리 입력
        int st_category = sc.nextInt();
        sc.nextLine();
        // 입력받은 값으로 DAO의 selectCategory() 호출
        ManageProductDAO dao = new ManageProductDAO();
        ArrayList<ManageProductVO> list = dao.selectCategory(st_category);

        // 카테고리별 품목 리스트 출력
        // 전체 품목 리스트 출력
        if (list.size() == 0) System.out.println("검색 결과 없음.!");
        for (ManageProductVO vo : list) {
            System.out.println(vo);
        }
        System.out.println("----------------------------------");
//        sc.close();
    }

    // 키워드 검색 조회
    private void selectProductListByKeyword() throws Exception {
        // 화면 출력
        Scanner sc = new Scanner(System.in);
        // 키워드 입력
        System.out.println("키워드를 입력하시오 >> ");
        String st_name = sc.next(); //한 단어 입력
        sc.nextLine();
        // 입력받은 값으로 DAO의 selectKeyword() 호출
        ManageProductDAO dao = new ManageProductDAO();
        ArrayList<ManageProductVO> list = dao.selectKeyword(st_name);

        //for문 출력
        for (ManageProductVO vo : list) {
            System.out.println(vo);
        }
//        sc.close();
    }

    // 품목 추가 메서드
    private void insertProduct() throws Exception {
        // 품목 추가에 대한 정보 입력
        Scanner sc = new Scanner(System.in);

        System.out.println("추가할 품목의 정보를 입력하세요.");

        System.out.print("재고물품명: ");
        String st_name = sc.nextLine();

        System.out.print("가격: ");
        int st_price = sc.nextInt();
        sc.nextLine();
        // 본사 물품관리에서 품목 추가를 하는 것이기 때문에 재고수량은 0 고정, 구분자도 0 고정임.
//        System.out.print("재고수량: ");
//        int st_quantity = sc.nextInt();
//        sc.nextLine(); // 버퍼 비우기
//
//        System.out.print("구분자: ");
//        int st_owner = sc.nextInt();

        System.out.print("카테고리: ");
        int st_category = sc.nextInt();

        System.out.print("단위(g, mg, EA): ");
        String st_unit = sc.next();

//        System.out.print("발주가능여부: ");
//        int st_state = sc.nextInt();

        ManageProductVO newProduct = new ManageProductVO(st_name, st_price, 0, 0, st_category, st_unit, 1);
        ManageProductDAO dao = new ManageProductDAO();

        // 입력받은 값으로 DAO의 insert() 호출
        int result = dao.insert(newProduct);
        if (result == 1) {
            System.out.println("품목이 성공적으로 추가되었습니다.");
        } else {
            System.out.println("품목 추가 중 오류가 발생했습니다: ");
        }
//        sc.close();
    }

}
