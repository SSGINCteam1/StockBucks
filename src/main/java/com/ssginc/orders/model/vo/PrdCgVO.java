package com.ssginc.orders.model.vo;

import lombok.Data;

@Data
public class PrdCgVO { //제조상품 카테고리
    private int prdCgNo; //제조상품 카테고리 번호
    private String prdCgName; //카테고리명
    private boolean prdCgStatus; //활성화 여부
}
