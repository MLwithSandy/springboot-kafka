package com.course.kafka.broker.producer;

import com.course.kafka.broker.message.OrderMessage;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderProducer {

  private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

  @Autowired private KafkaTemplate<String, OrderMessage> kafkaTemplate;

  private ProducerRecord<String, OrderMessage> buildProducerRecord(OrderMessage message) {
    int surpriseBonus = StringUtils.startsWithIgnoreCase(message.getOrderLocation(), "A") ? 25 : 15;

    List<Header> headers = new ArrayList<>();
    var surpriseBonusHeader =
        new RecordHeader("SurpriseBonus", Integer.toString(surpriseBonus).getBytes());
    headers.add(surpriseBonusHeader);

    return new ProducerRecord<String, OrderMessage>(
        "t.commodity.order", null, message.getOrderNumber(), message, headers);
  }

  public void publish(OrderMessage orderMessage) {
      var producerRecord = buildProducerRecord(orderMessage);
    kafkaTemplate
        .send(producerRecord)
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
