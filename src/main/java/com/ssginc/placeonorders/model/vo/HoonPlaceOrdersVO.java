package com.ssginc.placeonorders.model.vo;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoonPlaceOrdersVO {

    private int poNo;

    private String poDate;

    private int poTotal;

    private int usersNo;

}
