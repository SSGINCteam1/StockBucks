package com.ssginc.orders.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrdOptDTO {
    private int pNo;
    private int optCategoryNo;
    private String optCategoryName;
    private List<PrdOptDetailDTO> optionDetails;
}
