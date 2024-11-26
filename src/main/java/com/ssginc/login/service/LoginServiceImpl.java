package com.ssginc.login.service;

import com.ssginc.login.model.dao.LoginDAO;
import com.ssginc.login.model.dto.UsersDTO;
import com.ssginc.login.model.vo.UsersVO;
import com.ssginc.util.DBConnectionMgr;

import java.sql.Connection;

public class LoginServiceImpl implements LoginService {

    Connection con;
    DBConnectionMgr dbcp;

    LoginDAO loginDAO = new LoginDAO();

    public LoginServiceImpl() {
        dbcp = new DBConnectionMgr();
    }

    @Override
    public int insertUsers(UsersDTO user) {
        int res = 0;
        try {
            con = dbcp.getConnection();
            res = loginDAO.insertUsers(con, user);

            if (res == 1) {
                con.commit();
                System.out.println("회원가입이 성공했습니다.");
            } else {
                con.rollback();
                System.out.println("회원가입이 실패했습니다. 다시 시도해주세요.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    @Override
    public UsersDTO matchUsersId(String id) {
        UsersDTO user = null;
        try {
            con = dbcp.getConnection();
            user = loginDAO.matchUsersId(con, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public boolean matchUsersPw(UsersDTO users, String pwd) {
        return pwd.equals(users.getUsersPw());
    }

}
