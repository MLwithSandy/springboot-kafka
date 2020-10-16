package com.course.kafka.command.service;

import com.course.kafka.api.request.OrderRequest;
import com.course.kafka.command.action.OrderAction;
import com.course.kafka.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderAction action;

    public String saveOrder(OrderRequest request) {
        // 1. convert orderRequest to Order
        Order order = action.convertToOrder(request);

        // 2. Save Order to database

        action.saveToDatabase(order);

        // 3. flatten item & order as kafka message and publish

        order.getItems().forEach(action:: publishToKafka);

        // 4. return order number (auto generated)

        return order.getOrderNumber();




    }
}
