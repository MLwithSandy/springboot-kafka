package com.course.kafkaconsumer.config;

import com.course.kafkaconsumer.entity.CarLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

@Configuration
public class KafkaConfig {

  @Autowired private KafkaProperties kafkaProperties;

  @Bean
  public ConsumerFactory<Object, Object> consumerFactory() {
    var properties = kafkaProperties.buildConsumerProperties();

    properties.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "120000");
    return new DefaultKafkaConsumerFactory<Object, Object>(properties);
  }

  @Bean(name = "farLocationContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> farLocationContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
    var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
    configurer.configure(factory, consumerFactory());
    factory.setRecordFilterStrategy(new RecordFilterStrategy<Object, Object>() {

      ObjectMapper objectMapper = new ObjectMapper();
      @SneakyThrows
      @Override
      public boolean filter(ConsumerRecord<Object, Object> consumerRecord) {
        var carLocation = objectMapper.readValue(consumerRecord.value().toString(), CarLocation.class);

        return carLocation.getDistance() <= 100;
      }
    });
    return factory;
  }
}
