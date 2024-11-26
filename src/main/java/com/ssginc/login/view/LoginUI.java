package com.ssginc.login.view;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.model.dto.UsersDTOTest;
import com.ssginc.login.service.LoginService;
import com.ssginc.login.service.LoginServiceImpl;

import java.util.Scanner;

public class LoginUI {
    Scanner sc;
    LoginService loginService;

    public LoginUI() {
        sc = new Scanner(System.in);
        loginService = new LoginServiceImpl();
    }

    public void signUp() {
        System.out.println("Enter your ID: ");
        String id = sc.nextLine();
        System.out.println("Enter your password: ");
        String password = sc.nextLine();
        System.out.println("Enter your role(숫자 입력): 본사(0) / 매니저(1) / 사원(2)");
        int role = sc.nextInt();
        System.out.println("Enter your name: ");
        String name = sc.nextLine();

        UsersDTOTest user = new UsersDTOTest();

        user.setUsersId(id);
        user.setUsersPw(password);
        user.setUsersRole(role);
        user.setUsersName(name);


    }

    public UsersDTO loginMenu() {
        System.out.println("Welcome to Login Page");

        String id = inputId();
        String pw = inputPw();

        UsersDTO user = loginService.matchUsersId(id);

        if (user == null) {
            System.out.println("아이디가 일치하지 않습니다.");
            return null;
        } else if (!loginService.matchUsersPw(user, pw)){
            System.out.println("비밀번호가 일치하지 않습니다.");
            return null;
        }

        System.out.println("로그인에 성공하였습니다.");

        return user;
    }

    public String inputId(){
        System.out.print("아이디 : ");
        String userId = sc.nextLine();
        return userId;
    }

    public String inputPw(){
        System.out.print("패스워드 : ");
        String pw = sc.nextLine();
        return pw;
    }
}
