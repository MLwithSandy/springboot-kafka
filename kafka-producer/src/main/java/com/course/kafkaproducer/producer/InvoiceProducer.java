package com.course.kafkaproducer.producer;

import com.course.kafkaproducer.entity.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//@Service
public class InvoiceProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public void send(Invoice invoice) throws JsonProcessingException {
        var json = objectMapper.writeValueAsString(invoice);
        kafkaTemplate.send("t_invoice", invoice.getNumber(), json);
    }
}
