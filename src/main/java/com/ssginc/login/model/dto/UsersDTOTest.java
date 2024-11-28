package com.ssginc.login.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UsersDTOTest {
    private int usersNo; // 유저번호
    private String usersId; // 유저아이디
    private String usersPw; // 유저비밀번호
    private int usersRole; // 유저권한
    private String usersName; // 유저 이름
    private String usersBirth;
}
