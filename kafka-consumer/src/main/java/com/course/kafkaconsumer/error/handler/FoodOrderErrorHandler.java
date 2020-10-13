package com.course.kafkaconsumer.error.handler;

import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service(value="myFoodErrorHandler")
public class FoodOrderErrorHandler implements ConsumerAwareListenerErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(FoodOrderErrorHandler.class);

  @Override
  public Object handleError(
      Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
    log.warn(
        "Food Order error. Pretending to sent to elasticSearch : {}, because : {}",
        message.getPayload(),
        e.getMessage());
    if(e.getCause() instanceof RuntimeException){
      throw e;
    }
    return null;
  }
}
