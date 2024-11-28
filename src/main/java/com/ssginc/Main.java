package com.ssginc;

import com.ssginc.common.view.CommonUI;
import com.ssginc.login.view.LoginUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginUI loginUI = new LoginUI();

        int choice = displayStartMenu(scanner);

        switch (choice) {
            case 1:
                loginUI.signUp();
                break;
            case 2:
                loginUI.loginMenu();
                break;
            case 3:
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 입력입니다. 프로그램을 종료합니다.");
                System.exit(0);
        }
    }

    private static int displayStartMenu(Scanner scanner) {
        System.out.println("\tStockBucks 재고 입출고 관리 시스템\n");
        System.out.println(getAsciiArt());
        System.out.println("===================================\n");
        System.out.println("1. 회원 가입    2. 로그인    3. 종료");
        System.out.print("\n>>(숫자 입력): ");
        return scanner.nextInt();
    }



    private static String getAsciiArt() {
        return """
                ⠀⠀⠀⠀⠀⢀⣠⣴⣾⣿⣿⣿⣿⣿⠉⣿⣿⣿⣿⣿⣷⣦⣄⡀⠀⠀⠀⠀⠀⠀
                ⠀⠀⠀⠀⢀⣴⣿⣿⣿⣿⣿⣿⣿⣦⠀⠀⠀⣴⣿⣿⣿⣿⣿⣿⣿⣦⡀⠀⠀⠀⠀
                ⠀⠀⠀⣴⣿⣿⣿⣿⣿⠿⣧⠀⠙⠋⣠⣶⣄⠙⠋⠀⣼⠿⣿⣿⣿⣿⣿⣦⠀⠀⠀
                ⠀⢀⣾⡟⢿⣿⣿⣿⣿⣆⠀⢀⣀⣤⣤⣤⣤⣤⣀⡀⠀⣰⣿⣿⣿⣿⡿⢻⣷⡀⠀
                ⠀⣾⣿⠁⠈⠿⠛⠛⣻⣿⣟⡟⣽⣁⡀⠀⢀⣈⣯⢻⣻⣿⣟⠛⠛⠿⠁⠈⣿⣷⠀
                ⢸⣿⠃⠀⠀⠀⣠⣾⡟⡞⣾⢸⡿⠾⠿⠀⡿⠷⢿⡆⣷⢳⢻⣷⣄⠀⠀⠀⠘⣿⡇
                ⣟⣡⣴⠶⠟⣿⣿⣿⡟⡇⢿⡈⣇⠀⠀⠤⠀⠀⣸⢁⡿⢸⢺⣿⣿⣿⠻⠶⣦⣌⣻
                ⠉⣉⣠⣶⣶⣿⣿⣿⣇⢿⡘⣷⠸⣦⡈⠛⢁⣴⠇⣾⢃⡿⣸⣿⣿⣿⣶⣶⣄⣉⠉
                ⠛⠛⣉⣤⣿⣿⣿⣿⡟⣼⢃⡿⢀⡿⠛⠛⠛⢿⡀⢿⡘⣧⢻⣿⣿⣿⣿⣤⣉⠛⠛
                ⠸⠿⢋⣉⣿⣿⣿⢋⡾⣡⡞⢡⡞⠁⠀⠀⠀⠈⢳⡌⢳⡌⢷⡙⣿⣿⣿⣉⡙⠿⠇
                ⠀⢶⠟⣋⣿⠿⠋⢸⡇⢿⡄⢿⡄⠀⠀⠀⠀⠀⢠⡿⢠⡿⢸⡇⠙⠿⣿⣙⠻⡶⠀
                ⠀⠀⢾⠟⣃⠀⠀⣠⣿⣌⢳⡌⢻⣄⠀⠀⠀⣠⡟⢡⡞⣡⣿⣄⠀⠀⣘⠻⡷⠀⠀
                ⠀⠀⠀⠺⢋⣿⣿⣿⡟⣿⠀⣿⠀⣿⠄⠀⠠⣿⠀⣿⠀⣿⢻⣿⣿⣿⡙⠗⠀⠀⠀
                ⠀⠀⠀⠀⠈⠋⣽⠏⣼⠃⣼⠏⣰⡟⠀⠀⠀⢻⣆⠹⣧⠘⣧⠹⣯⠙⠁⠀⠀⠀⠀
                ⠀⠀⠀⠀⠀⠀⠀⠘⠋⢸⡏⢠⣿⠀⠀⠀⠀⠀⣿⡄⢹⡇⠙⠃⠀⠀⠀⠀⠀⠀⠀
                """;
    }
}
