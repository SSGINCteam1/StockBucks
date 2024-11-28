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
}
