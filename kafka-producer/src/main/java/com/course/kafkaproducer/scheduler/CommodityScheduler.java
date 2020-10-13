package com.course.kafkaproducer.scheduler;

import com.course.kafkaproducer.entity.Commodity;
import com.course.kafkaproducer.producer.CommodityProducer;
import com.course.kafkaproducer.service.CommodityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// @Service
public class CommodityScheduler {
  private RestTemplate restTemplate = new RestTemplate();

  @Autowired private CommodityProducer producer;

  @Scheduled(fixedDelay = 5000)
  public void fetchCommodities() {
    var commodities =
        restTemplate
            .exchange(
                "http://localhost:8080/api/commodity/v1/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Commodity>>() {})
            .getBody();
    commodities.forEach(
        c -> {
          try {
            producer.sendMessage(c);
          } catch (JsonProcessingException e) {
            e.printStackTrace();
          }
        });
  }
}
