package com.course.kafkaconsumer.consumer;

import com.course.kafkaconsumer.entity.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// @Service
public class InvoiceConsumer {
  private static final Logger log = LoggerFactory.getLogger(InvoiceConsumer.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  @KafkaListener(topics = "t_invoice", containerFactory = "invoiceDltContainerFactory")
  public void consume(String message) throws JsonProcessingException {
    var invoice = objectMapper.readValue(message, Invoice.class);

    if (invoice.getAmount() < 1) {
      throw new IllegalArgumentException(
          "Invalid amount : " + invoice.getAmount() + " for invoice : " + invoice.getNumber());
    }
    log.info("Processing invoide: {}", invoice);
  }
}
