package com.ssginc.orders.model.dao;

import com.ssginc.orders.model.dto.PrdCgDTO;
import com.ssginc.orders.model.dto.PrdOptDTO;
import com.ssginc.orders.model.dto.PrdOptDetailDTO;
import com.ssginc.util.HikariCPDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PrdOptDAO {
    DataSource dataSource;

    public PrdOptDAO() {
        dataSource = HikariCPDataSource.getInstance().getDataSource();
    }

    public ArrayList<PrdOptDTO> selectPrdOpt(int pNo) {
        ArrayList<PrdOptDTO> selectPrdOpt = new ArrayList<>();

        String sql = """ 
             SELECT p.p_no, o.category_no, o.optcg_name, ot.opt_no, ot.opt_name, ot.opt_price
            FROM products p
            JOIN prd_optcg o ON o.p_no = p.p_no
            JOIN opt_category oc ON o.category_no = oc.category_no
            JOIN opt ot ON ot.category_no = oc.category_no
            WHERE p.p_no = ?
            ORDER BY p.p_no, o.category_no, ot.opt_no
                """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pNo);

            try (ResultSet rs = ps.executeQuery()) {
                int prevCategoryNo = -1; // 이전 카테고리 번호
                PrdOptDTO currentPrdOptDTO = null; // 현재 PrdOptDTO
                ArrayList<PrdOptDetailDTO> details = null; // 옵션 상세 리스트

                while (rs.next()) {
                    int currCategoryNo = rs.getInt("category_no");

                    // 카테고리가 변경될 때 새로운 PrdOptDTO 생성
                    if (currCategoryNo != prevCategoryNo) {
                        if (currentPrdOptDTO != null) {
                            // 이전 PrdOptDTO에 옵션 리스트를 설정
                            currentPrdOptDTO.setOptionDetails(details);
                            selectPrdOpt.add(currentPrdOptDTO);
                        }

                        // 새로운 PrdOptDTO 객체 생성
                        currentPrdOptDTO = new PrdOptDTO();
                        currentPrdOptDTO.setPNo(rs.getInt("p_no"));
                        currentPrdOptDTO.setOptCategoryNo(currCategoryNo);
                        currentPrdOptDTO.setOptCategoryName(rs.getString("optcg_name"));

                        // 새로운 옵션 상세 리스트 초기화
                        details = new ArrayList<>();
                    }

                    // PrdOptDetailDTO 생성 및 설정
                    PrdOptDetailDTO detail = new PrdOptDetailDTO();
                    detail.setOptionName(rs.getString("opt_name"));
                    detail.setOptionPrice(rs.getInt("opt_price"));

                    // 옵션 상세 리스트에 추가
                    details.add(detail);

                    // 이전 카테고리 번호 갱신
                    prevCategoryNo = currCategoryNo;
                }

                // 마지막 PrdOptDTO 객체를 리스트에 추가
                if (currentPrdOptDTO != null) {
                    currentPrdOptDTO.setOptionDetails(details);
                    selectPrdOpt.add(currentPrdOptDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectPrdOpt;
    }
}

