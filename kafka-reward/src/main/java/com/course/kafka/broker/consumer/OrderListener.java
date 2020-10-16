package com.course.kafka.broker.consumer;

import com.course.kafka.broker.message.OrderMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//@Service
public class OrderListener {
  private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

  @KafkaListener(topics = "t.commodity.order")
  public void listen(ConsumerRecord<String, OrderMessage> consumerRecord) {
    var headers = consumerRecord.headers();
    var orderMessage = consumerRecord.value();

    log.info(
        "Processing order {}, credit card number ()",
        orderMessage.getOrderNumber(),
        orderMessage.getCreditCardNumber());
    log.info("Headers are :");
    headers.forEach(h -> log.info("key : {}, value : {}", h.key(), h.value()));
    var bonusPercentage =
        Double.parseDouble(new String((headers.lastHeader("SurpriseBonus").value())));
    var bonusAmount =
        (bonusPercentage / 100) * orderMessage.getPrice() + orderMessage.getQuantity();
    log.info("Suprose bonus is  {}", bonusAmount);
  }
}
