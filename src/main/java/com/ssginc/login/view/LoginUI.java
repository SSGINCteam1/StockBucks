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
            loginMenu();
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
}
