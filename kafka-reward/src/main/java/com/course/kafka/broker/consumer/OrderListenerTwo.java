package com.course.kafka.broker.consumer;

import com.course.kafka.broker.message.OrderMessage;
import com.course.kafka.broker.message.OrderReplyMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class OrderListenerTwo {
  private static final Logger log = LoggerFactory.getLogger(OrderListenerTwo.class);

  @KafkaListener(topics = "t.commodity.order")
  @SendTo("t.commodity.order-reply")
  public OrderReplyMessage listen(ConsumerRecord<String, OrderMessage> consumerRecord) {
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

    var replyMessage = new OrderReplyMessage();
    replyMessage.setReplyMessage("Order " + orderMessage.getOrderNumber() + " processed.");
    return replyMessage;
  }
}
