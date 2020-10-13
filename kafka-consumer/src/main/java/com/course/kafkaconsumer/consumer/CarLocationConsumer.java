package com.course.kafkaconsumer.consumer;

import com.course.kafkaconsumer.entity.CarLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// @Service
public class CarLocationConsumer {
  private static final Logger log = LoggerFactory.getLogger(CarLocationConsumer.class);

  private ObjectMapper objectMapper = new ObjectMapper();

  @KafkaListener(topics = "t_location", groupId = "cg-all-location")
  public void listenAll(String message) throws JsonProcessingException {
    var carLocation = objectMapper.readValue(message, CarLocation.class);
    log.info("listenAll : {}", carLocation);
  }

  @KafkaListener(
      topics = "t_location",
      groupId = "cg-far-location",
      containerFactory = "farLocationContainerFactory")
  public void listenFar(String message) throws JsonProcessingException {
    var carLocation = objectMapper.readValue(message, CarLocation.class);
    log.info("listenFar : {}", carLocation);
  }
}
