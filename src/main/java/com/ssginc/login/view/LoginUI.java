package com.ssginc.login.view;

import com.ssginc.common.view.CommonUI;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.model.dto.UsersDTOTest;
import com.ssginc.login.service.LoginService;
import com.ssginc.login.service.LoginServiceImpl;

import java.util.Scanner;

public class LoginUI {
    private final Scanner sc;
    private final LoginService loginService;

    public LoginUI() {
        this.sc = new Scanner(System.in);
        this.loginService = new LoginServiceImpl();
    }

    public void signUp() {
        System.out.println("===================================\n");
        System.out.println("[회원가입]");

        String id = promptInput("아이디 입력 >> ");
        String password = promptInput("비밀번호 입력 >> ");
        int role = promptInputForInt("권한 입력 : 본사(0) / 매니저(1) / 사원(2)\n>>(숫자입력) ");
        String name = promptInput("이름 입력 >> ");
        String birthday = promptInput("생년월일 입력(예시 : 1996-02-07) >> ");

        UsersDTOTest user = new UsersDTOTest();
        user.setUsersId(id);
        user.setUsersPw(password);
        user.setUsersRole(role);
        user.setUsersName(name);
        user.setUsersBirth(birthday);

        int res = loginService.insertUsers(user);

        System.out.println(res == 1 ? "회원가입이 성공했습니다." : "회원가입이 실패했습니다. 다시 시도해주세요.");
    }

    public UsersDTO loginMenu() {
        int choice = displayStartMenu(sc);
        sc.nextLine(); // 버퍼 정리
        UsersDTO user = null;
        switch (choice) {
            case 1:
                this.signUp();
                break;
            case 2:
                user = this.login();
                break;
            case 3:
                CommonUI.displayExitMessage();
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 입력입니다. 프로그램을 종료합니다.");
                System.exit(0);
        }

        return user;
    }

    private UsersDTO login(){
        System.out.println("===================================\n");
        System.out.println("[로그인] \n");

        String id = inputId();
        String pw = inputPw();

        UsersDTO user = loginService.matchUsersId(id);

        if (user == null) {
            handleLoginFailure("아이디가 일치하지 않습니다.");
            return null;
        } else if (!loginService.matchUsersPw(user, pw)) {
            handleLoginFailure("비밀번호가 일치하지 않습니다.");
            return null;
        }

        System.out.println("\n로그인에 성공하였습니다.\n");
        welcomeUser(user);

        return user;
    }

    private void handleLoginFailure(String message) {
        System.out.println("\n" + message + "\n");
        CommonUI.displayAgainOrExitMessage();
        if (sc.nextInt() == 1) {
            sc.nextLine(); // 버퍼 정리
            login();
        } else {
            CommonUI.displayExitMessage();
        }
    }

    private void welcomeUser(UsersDTO user) {
        String role = null;
        switch (user.getUsersRole()) {
            case "0" :
                role = "본사";
                break;
            case "1" :
                role = "매니저";
                break;
            default :
                role = "사원";
        }
        System.out.println(user.getUsersName() + "(" + role + ")" + "님 환영합니다.");
    }

    private String promptInput(String message) {
        System.out.print(message);
        return sc.nextLine();
    }

    private int promptInputForInt(String message) {
        System.out.print(message);
        int value = sc.nextInt();
        sc.nextLine(); // 버퍼 정리
        return value;
    }

    public String inputId() {
        return promptInput("아이디 입력 >> ");
    }

    public String inputPw() {
        return promptInput("\n패스워드 입력 >> ");
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
