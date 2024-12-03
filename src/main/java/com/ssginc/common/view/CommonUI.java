package com.ssginc.common.view;

public class CommonUI {
    public static void displayExitMessage() {
        System.out.println("시스템을 종료합니다.");
        System.out.println("이용해주셔서 감사합니다.");
    }

    public static void displayAgainOrExitMessage() {
        System.out.println("1. 다시 시도    2. 종료");
        System.out.print("\n>>");
    }

    public static void displayWrongSelectMessage() {
        System.out.println("잘못된 입력입니다. 다시 입력해주세요");
    }

    public static void displayGoBackMessage() {
        System.out.println("상위 메뉴로 이동합니다.");
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
}
