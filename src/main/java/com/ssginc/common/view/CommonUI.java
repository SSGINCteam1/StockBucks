package com.ssginc.common.view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CommonUI {
    private static int consoleWidth = 60;

    public static void displayExitMessage() {
        System.out.println("\n>> [시스템을 종료합니다.]");
        System.out.println("[이용해주셔서 감사합니다.]");
    }

    public static void displayAgainOrExitMessage() {
        System.out.println("1. 다시 시도    2. 종료");
        System.out.print("\n>>");
    }

    public static void displayWrongSelectMessage() {
        System.out.println("\n>> [잘못된 입력입니다. 다시 입력해주세요.]");
    }

    public static void displayGoBackMessage() {
        System.out.println("\n>> [상위 메뉴로 이동합니다.]");
    }

    public static void printCentered(String text) {
        int padding = (consoleWidth - text.length()) / 2;
        String format = "%" + padding + "s%s%" + padding + "s";
        System.out.printf(format, "", text, "");
        System.out.println(); // 줄바꿈
    }

    /**
     * 전체 주문 내역 목록 페이지 처리 메서드
     * @param currentPage
     * @param totalPages
     */
    public static void displayPageBar(int currentPage, int totalPages) {
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
     * 숫자만 입력받을 수 있도록 예외처리
     * @return
     */
    public static int safeInput(Scanner sc) {
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
