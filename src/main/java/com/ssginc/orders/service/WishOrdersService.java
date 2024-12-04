package com.ssginc.orders.service;

import com.ssginc.orders.model.dto.WishProductsDTO;

import java.util.List;

public interface WishOrdersService {
    int insertOrders(List<WishProductsDTO> products, int usersNo);
}
