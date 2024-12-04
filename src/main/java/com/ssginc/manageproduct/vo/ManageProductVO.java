package com.ssginc.manageproduct.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
public class ManageProductVO {

    // AUTO_INCREMENT라 생성자에서는 필요가 없음
    private int st_no;
    private String st_name;
    private int st_price;
    private int st_quantity;
    private int st_owner;
    private int st_category;
    private String st_unit;
    private int st_state;

    public ManageProductVO(String st_Name, int st_price, int st_Quantity, int st_Owner, int st_Category, String st_Unit, int st_State) {
        this.st_name = st_Name;
        this.st_price = st_price;
        this.st_quantity = st_Quantity;
        this.st_owner = st_Owner;
        this.st_category = st_Category;
        this.st_unit = st_Unit;
        this.st_state = st_State;
    }

    public ManageProductVO() {

    }


    @Override
    public String toString() {
        return "" +
                "물품번호='" + st_no + '\'' +
                ", 재고물품명='" + st_name + '\'' +
                ", 가격=" + st_price +
                ", 재고수량=" + st_quantity +
                ", 구분자=" + st_owner +
                ", 카테고리=" + st_category +
                ", 단위='" + st_unit + '\'' +
                ", 발주가능여부=" + st_state
                ;
    }
}
