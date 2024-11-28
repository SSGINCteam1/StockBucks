package com.ssginc.login.service;

import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.model.dto.UsersDTOTest;
import com.ssginc.login.model.vo.UsersVO;

public interface LoginService {
    // 회원가입
    int insertUsers(UsersDTOTest user);

    // 아이디 일치 여부 확인
    UsersDTO matchUsersId(String id);

    // 비밀번호 일치 여부 확인 => 맞을 시 Users 객체 반환
    boolean matchUsersPw(UsersDTO users, String pwd);
}
