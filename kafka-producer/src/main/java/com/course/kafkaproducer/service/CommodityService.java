package com.course.kafkaproducer.service;

import com.course.kafkaproducer.entity.Commodity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CommodityService {

  private static final Map<String, Commodity> COMMIDITY_BASE = new HashMap<String, Commodity>();
  private static final String COPPER = "copper";
  private static final String GOLD = "gold";

  // max adjustment for price (base price * max adjustment
  private static final double MAX_ADJUSTMENT = 1.05;

  // min adjustment for price (base price * min adjustment
  private static final double MIN_ADJUSTMENT = 0.95;

  static {
    var timestamp = System.currentTimeMillis();
    COMMIDITY_BASE.put(GOLD, new Commodity(GOLD, 1_407.25, "ounce", timestamp));
    COMMIDITY_BASE.put(COPPER, new Commodity(COPPER, 5_900.57, "tonne", timestamp));
  }

  public Commodity createDummyCommodity(String name) {
    if (!COMMIDITY_BASE.keySet().contains(name)) {
      throw new IllegalArgumentException("Invalid commodity : " + name);
    }
    var commodity = COMMIDITY_BASE.get(name);
    var basePrice = commodity.getPrice();
    var newPrice =
        basePrice * ThreadLocalRandom.current().nextDouble(MIN_ADJUSTMENT, MAX_ADJUSTMENT);
    commodity.setPrice(newPrice);
    commodity.setTimestamp(System.currentTimeMillis());
    return commodity;
  }

  public List<Commodity> createDummyCommodities() {
    var result = new ArrayList<Commodity>();
    COMMIDITY_BASE.keySet().forEach(c -> result.add(createDummyCommodity(c)));
    return result;
  }
}
