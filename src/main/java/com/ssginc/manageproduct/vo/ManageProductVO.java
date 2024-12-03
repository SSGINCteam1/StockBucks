package com.ssginc.manageproduct.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ManageProductVO {

    private int st_no;
    private String st_name;
    private int st_price;
    private int st_quantity;
    private int st_owner;
    private int st_category;
    private String st_unit;
    private int st_state;

}
