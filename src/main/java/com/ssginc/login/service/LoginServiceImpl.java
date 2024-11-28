package com.ssginc.login.service;

import com.ssginc.login.model.dao.LoginDAO;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.model.dto.UsersDTOTest;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class LoginServiceImpl implements LoginService {
    DataSource dataSource;
    LoginDAO loginDAO;

    public LoginServiceImpl() {
        loginDAO = new LoginDAO();
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    @Override
    public int insertUsers(UsersDTOTest user) {
        int res = 0;
        try(Connection con = dataSource.getConnection()) {
            res = loginDAO.insertUsers(con, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public UsersDTO matchUsersId(String id) {
        UsersDTO user = null;
        try(Connection con = dataSource.getConnection()) {
            user = loginDAO.matchUsersId(con, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean matchUsersPw(UsersDTO users, String pwd) {
        return pwd.equals(users.getUsersPw());
    }

}
