package com.course.kafka.command.action;

import com.course.kafka.api.request.OrderItemRequest;
import com.course.kafka.api.request.OrderRequest;
import com.course.kafka.broker.message.OrderMessage;
import com.course.kafka.broker.producer.OrderProducer;
import com.course.kafka.entity.Order;
import com.course.kafka.entity.OrderItem;
import com.course.kafka.repository.OrderItemRepository;
import com.course.kafka.repository.OrderRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderAction {

    @Autowired
    private OrderProducer producer;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    public Order convertToOrder(OrderRequest request) {
        var result = new Order();
        result.setCreditCardNumber(request.getCreditCardNumber());
        result.setOrderLocation(request.getOrderLocation());
        result.setOrderDateTime(LocalDateTime.now());
        result.setOrderNumber(RandomStringUtils.randomAlphanumeric(8).toUpperCase());

        List<OrderItem> items = request.getItems().stream().map(this::convertToOrderItem).collect(Collectors.toList());
        items.forEach(item -> item.setOrder(result));
        result.setItems(items);

        return result;
    }

    private OrderItem convertToOrderItem(OrderItemRequest orderItemRequest) {
        var result = new OrderItem();

        result.setItemName(orderItemRequest.getItemName());
        result.setPrice(orderItemRequest.getPrice());
        result.setQuantity(orderItemRequest.getPrice());

        return result;
    }

    public void saveToDatabase(Order order) {
        orderRepository.save(order);
        order.getItems().forEach(orderItemRepository::save);
    }

    public void publishToKafka(OrderItem item) {
        var orderMessage = new OrderMessage();
        orderMessage.setItemName(item.getItemName());
        orderMessage.setPrice(item.getPrice());
        orderMessage.setQuantity(item.getQuantity());
        orderMessage.setOrderDateTime(item.getOrder().getOrderDateTime());
        orderMessage.setOrderLocation(item.getOrder().getOrderLocation());
        orderMessage.setOrderNumber(item.getOrder().getOrderNumber());
        orderMessage.setCreditCardNumber(item.getOrder().getCreditCardNumber());

        producer.publish(orderMessage);
    }
}
