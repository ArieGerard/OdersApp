package com.gcu.activity1.data;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.gcu.activity1.models.Mapper;
import com.gcu.activity1.models.OrderModel;

@Service
public class OrdersDataService implements DataAccessInterface<OrderModel> {

    // add a data member for the repository using dependency injection
    @Autowired
    private OrdersRepository ordersRepository;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    // constructor for dependency injection
    public OrdersDataService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public OrderModel getById(int id) {
        OrderEntity orderEntity = ordersRepository.findById(id).orElse(null);
        if (orderEntity == null) {
            return null;
        }
        return Mapper.toModel(orderEntity);
    }

    @Override
    public Iterable<OrderModel> getAll() {
        ArrayList<OrderModel> orderModels = new ArrayList<OrderModel>();
        Iterable<OrderEntity> orderEntities = ordersRepository.findAll();
        for (OrderEntity orderEntity : orderEntities) {
            orderModels.add(Mapper.toModel(orderEntity));
        }
        return orderModels;
    }

    @Override
    public OrderModel create(OrderModel item) {
        OrderEntity orderEntity = ordersRepository.save(Mapper.toEntity(item));
        return Mapper.toModel(orderEntity);
    }

    @Override
    public OrderModel update(OrderModel item) {
        OrderEntity orderEntity = ordersRepository.save(Mapper.toEntity(item));
        return Mapper.toModel(orderEntity);
    }

    @Override
    public boolean deleteById(int id) {
        ordersRepository.deleteById(id);
        return true;
    }
}
