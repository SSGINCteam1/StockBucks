package com.ssginc.login.model.vo;

import lombok.Data;

@Data
public class UsersVO {
    private int usersNo; // 유저번호
    private String usersId; // 유저아이디
    private String usersPw; // 유저비밀번호
    private String usersRole; // 유저권한
    private String usersName; // 유저 이름
    private boolean isActive; // 유저 활성화 여부
}
