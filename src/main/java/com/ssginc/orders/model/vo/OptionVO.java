package com.ssginc.orders.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionVO {
    private int optionNo;
    private String optionName;
    private int optionPrice;
    private int optionConsume;
    private int categoryNo;
    private boolean isActive;
}
