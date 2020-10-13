package com.course.kafkaconsumer.consumer;

import com.course.kafkaconsumer.entity.Commodity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

//@Service
public class CommodityNotificationConsumer {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CommodityDashboardConsumer.class);

    @KafkaListener(topics = "t_comodity", groupId = "cg-notification")
    public void consume(String message)
            throws JsonProcessingException, JsonMappingException, IOException {
        var commodity = objectMapper.readValue(message, Commodity.class);
        log.info("Notification logic for {}", commodity);
    }
}
