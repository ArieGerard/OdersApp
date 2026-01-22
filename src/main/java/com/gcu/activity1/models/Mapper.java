package com.gcu.activity1.models;

import com.gcu.activity1.data.OrderEntity;

public class Mapper {

    public static OrderModel toModel(OrderEntity orderEntity) {
        return new OrderModel(orderEntity.getId(), orderEntity.getOrder_number(), orderEntity.getProduct_name(),
                orderEntity.getPrice(), orderEntity.getQuantity());
    }

    public static OrderEntity toEntity(OrderModel orderModel) {
        return new OrderEntity(orderModel.getId(), orderModel.getOrder_number(), orderModel.getProduct_name(),
                orderModel.getPrice(), orderModel.getQuantity());
    }
}
