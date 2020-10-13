package com.course.kafka.broker.producer;

import com.course.kafka.broker.message.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class OrderProducer {

  private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

  @Autowired private KafkaTemplate<String, OrderMessage> kafkaTemplate;

  public void publish(OrderMessage orderMessage) {
    kafkaTemplate
        .send("t_commodity_order", orderMessage.getOrderNumber(), orderMessage)
        .addCallback(
            new ListenableFutureCallback<SendResult<String, OrderMessage>>() {
              @Override
              public void onFailure(Throwable ex) {
                log.error(
                    "Order {}, item {} failed to publish, because {}",
                    orderMessage.getOrderNumber(),
                    orderMessage.getItemName(),
                    ex.getMessage());
                // do something else, may be inserting to log db
              }

              @Override
              public void onSuccess(SendResult<String, OrderMessage> stringOrderMessageSendResult) {
                log.info(
                    "Order {}, item {} published successfully",
                    orderMessage.getOrderNumber(),
                    orderMessage.getItemName());
              }
            });
    log.info(
        "Order {}, item {} published ", orderMessage.getOrderNumber(), orderMessage.getItemName());
  }
}
