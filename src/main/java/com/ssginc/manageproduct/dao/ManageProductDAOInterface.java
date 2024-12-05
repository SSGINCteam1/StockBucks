package com.ssginc.manageproduct.dao;

import com.ssginc.manageproduct.vo.ManageProductVO;

import java.util.ArrayList;

public interface ManageProductDAOInterface {
    ArrayList<ManageProductVO> selectAll() throws Exception;

    ArrayList<ManageProductVO> selectCategory(int st_category) throws Exception;

    ArrayList<ManageProductVO> selectKeyword(String st_name) throws Exception;

    boolean update(String st_name, int st_no) throws Exception;

    boolean delete(int st_no) throws Exception;

    int insert(ManageProductVO bag) throws Exception;
}
