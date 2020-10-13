package com.course.kafkaproducer;

import com.course.kafkaproducer.entity.Employee;
import com.course.kafkaproducer.entity.FoodOrder;
import com.course.kafkaproducer.entity.SimpleNumber;
import com.course.kafkaproducer.producer.*;
import com.course.kafkaproducer.service.ImageService;
import com.course.kafkaproducer.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

@SpringBootApplication
// @EnableScheduling
public class KafkaProducerApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(KafkaProducerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {}
}
