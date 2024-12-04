package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.PrdCgDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PrdCgDAO {
    DataSource dataSource;

    public PrdCgDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    // 음료 카테고리 조회
    public ArrayList<PrdCgDTO> selectPrdCg() {
        ArrayList<PrdCgDTO> selectPrdCg = new ArrayList<>();

        String sql = "SELECT prdcg_no, prdcg_name FROM prdcg ORDER BY prdcg_no";

        // try-with-resources를 사용하여 자동으로 자원을 닫도록 처리
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // PrdCgDTO 객체 생성 및 값 설정
                PrdCgDTO prdCgDTO = PrdCgDTO.builder().build();
                prdCgDTO.setPrdCgNo(rs.getInt("prdcg_no"));
                prdCgDTO.setPrdCgName(rs.getString("prdcg_name"));
                // DTO 객체를 ArrayList에 추가
                selectPrdCg.add(prdCgDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그 출력
        }
        return selectPrdCg;
    }
}
