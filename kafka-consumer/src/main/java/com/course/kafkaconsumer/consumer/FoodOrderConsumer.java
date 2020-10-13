package com.course.kafkaconsumer.consumer;

import com.course.kafkaconsumer.entity.FoodOrder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

// @Service
public class FoodOrderConsumer {

  private static final Logger log = LoggerFactory.getLogger(FoodOrderConsumer.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  private static final int MAX_AMOUNT_ORDER = 7;

  @KafkaListener(topics = "t_food_order", errorHandler = "myFoodErrorHandler")
  public void consume(String message) throws JsonParseException, JsonMappingException, IOException {
    var foodOrder = objectMapper.readValue(message, FoodOrder.class);

    if (foodOrder.getAmount() > MAX_AMOUNT_ORDER) {
      throw new IllegalArgumentException("Food order Amount is too many");
    }
    log.info("Food order valid: {}", foodOrder);
  }
}
