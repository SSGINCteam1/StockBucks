package com.ssginc.orders.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumptionDTO {
    // 소모량 저장하기 위한 DTO

    int stockNo; // 원자재 번호
    int consumption; // 소모량
}
