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
import java.util.concurrent.ThreadLocalRandom;

//@Service
public class CommodityDashboardConsumer {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CommodityDashboardConsumer.class);

    @KafkaListener(topics = "t_comodity", groupId = "cg-dashboard")
    public void consume(String message)
            throws JsonProcessingException, JsonMappingException, IOException, InterruptedException {
        var commodity = objectMapper.readValue(message, Commodity.class);

        Thread.sleep(ThreadLocalRandom.current().nextLong(500, 1000));
        log.info("Dashboard logic for {}", commodity);
    }
}
