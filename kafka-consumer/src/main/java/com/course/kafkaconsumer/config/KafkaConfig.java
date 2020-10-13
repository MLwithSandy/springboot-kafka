package com.course.kafkaconsumer.config;

import com.course.kafkaconsumer.entity.CarLocation;
import com.course.kafkaconsumer.error.handler.GlobalErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.FixedBackOff;

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
    factory.setRecordFilterStrategy(
        new RecordFilterStrategy<Object, Object>() {

          ObjectMapper objectMapper = new ObjectMapper();

          @SneakyThrows
          @Override
          public boolean filter(ConsumerRecord<Object, Object> consumerRecord) {
            var carLocation =
                objectMapper.readValue(consumerRecord.value().toString(), CarLocation.class);

            return carLocation.getDistance() <= 100;
          }
        });
    return factory;
  }

  @Bean(name = "kafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
    var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
    configurer.configure(factory, consumerFactory());

    factory.setErrorHandler(new GlobalErrorHandler());

    return factory;
  }

  private RetryTemplate createRetryTemplate() {
    var retryTemplate = new RetryTemplate();
    var retryPolicy = new SimpleRetryPolicy(3);

    retryTemplate.setRetryPolicy(retryPolicy);
    var backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(10_000);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    return retryTemplate;
  }

  @Bean(name = "imageRetryContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> imageRetryContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer) {
    var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
    configurer.configure(factory, consumerFactory());

    factory.setErrorHandler(new GlobalErrorHandler());
    factory.setRetryTemplate(createRetryTemplate());

    return factory;
  }

  @Bean(value = "invoiceDltContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> invoiceDltContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
      KafkaOperations<Object, Object> kafkaTemplate) {
    var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
    configurer.configure(factory, consumerFactory());

    var recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaTemplate, (record, ex) -> new TopicPartition("t_invoice_dlt", record.partition()));

    // 5 retry, 10 second interval for each retry
    var errorHandler = new SeekToCurrentErrorHandler(recoverer, new FixedBackOff(10_000, 5));

    factory.setErrorHandler(errorHandler);

    return factory;
  }
}
