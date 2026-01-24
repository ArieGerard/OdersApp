package com.gcu.activity1.models;

import com.gcu.activity1.data.OrderEntity;
import com.gcu.activity1.data.UserEntity;

public class Mapper {

    public static OrderModel toModel(OrderEntity orderEntity) {
        return new OrderModel(orderEntity.getId(), orderEntity.getOrder_number(), orderEntity.getProduct_name(),
                orderEntity.getPrice(), orderEntity.getQuantity());
    }

    public static OrderEntity toEntity(OrderModel orderModel) {
        return new OrderEntity(orderModel.getId(), orderModel.getOrder_number(), orderModel.getProduct_name(),
                orderModel.getPrice(), orderModel.getQuantity());
    }

    public static UserModel toModel(UserEntity userEntity) {
        return new UserModel(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getRole(),
                userEntity.isEnabled()
        );
    }

    public static UserEntity toEntity(UserModel userModel) {
        return new UserEntity(
                userModel.getId(),
                userModel.getUsername(),
                userModel.getPassword(),
                userModel.getRole(),
                userModel.isEnabled()
        );
    }

    public static UserModel registrationToUser(RegistrationModel registration) {
        return new UserModel(
                0,
                registration.getUsername(),
                registration.getPassword(),
                "ROLE_USER",
                true
        );
    }
}
