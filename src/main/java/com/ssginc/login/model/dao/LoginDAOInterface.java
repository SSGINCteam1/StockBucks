package com.ssginc.login.model.dao;

import com.ssginc.login.model.dto.UsersDTO;

import java.sql.Connection;

public interface LoginDAOInterface {
    int insertUsers(Connection con, UsersDTO user);

    UsersDTO matchUsersId(Connection con, String id);
}
