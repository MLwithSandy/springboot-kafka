package com.course.kafka.broker.consumer;

import com.course.kafka.broker.message.DiscountMessage;
import com.course.kafka.broker.message.PromotionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = "t.commodity.promotion")
public class PromotionListener {

    private static final Logger log = LoggerFactory.getLogger(PromotionListener.class);

    @KafkaHandler
    public void listenPromotion(PromotionMessage message){
        log.info("Processing promotion : {}", message);
    }

    @KafkaHandler
    public void listenDiscount(DiscountMessage message){
        log.info("Processing discount : {}", message);
    }

}
