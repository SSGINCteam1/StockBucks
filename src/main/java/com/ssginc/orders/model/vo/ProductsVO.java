package com.ssginc.orders.model.vo;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class ProductsVO { //제조상품 테이블
    private int pNo; //제조상품 id
    private String pName; //제조상품 이름
    private int pPrice; //제조상품 가격
    private boolean pStatus; //판매가능 여부
    private int prdcgNo; //제조상품 카테고리번호 (외래키)
    private Date pReleaseDate; //출시일
}
