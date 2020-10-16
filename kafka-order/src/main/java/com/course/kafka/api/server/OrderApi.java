package com.course.kafka.api.server;

import com.course.kafka.api.request.OrderRequest;
import com.course.kafka.api.response.OrderResponse;
import com.course.kafka.command.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderApi {

    private static final Logger log = LoggerFactory.getLogger(OrderApi.class);

    @Autowired
    private OrderService service;

    @PostMapping(value="", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request){
        log.info("Order request received: {}", request);


        // 1. save order using service
        String orderNumber = service.saveOrder(request);


        // 2. create response
        OrderResponse orderResponse = new OrderResponse(orderNumber);

        // 3. return respose
        return ResponseEntity.ok().body(orderResponse);

    }
}
