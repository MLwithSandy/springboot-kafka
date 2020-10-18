package com.course.kafka.util;

import com.course.kafka.broker.message.OrderMessage;
import com.course.kafka.broker.message.OrderPatternMessage;
import com.course.kafka.broker.message.OrderRewardMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;

import java.util.Base64;

public class CommoditySteamUtil {

  public static OrderMessage maskCreditCard(OrderMessage original) {
    var masked = original.copy();
    masked.setCreditCardNumber(
        original.getCreditCardNumber().replaceFirst("\\d{12}", StringUtils.repeat('*', 12)));
    return masked;
  }

  public static OrderPatternMessage mapToOrderPattern(OrderMessage original) {
    var result = new OrderPatternMessage();
    result.setItemName(original.getItemName());
    result.setOrderDateTime(original.getOrderDateTime());
    result.setOrderLocation(original.getOrderLocation());
    result.setOrderNumber(original.getOrderNumber());

    var totalItemAmount = original.getQuantity() * original.getPrice();
    result.setTotalItemAmount(totalItemAmount);

    return result;
  }

  public static OrderRewardMessage mapToOrderReward(OrderMessage original) {
    var result = new OrderRewardMessage();

    result.setItemName(original.getItemName());
    result.setOrderDateTime(original.getOrderDateTime());
    result.setOrderLocation(original.getOrderLocation());
    result.setOrderNumber(original.getOrderNumber());
    result.setPrice(original.getPrice());
    result.setQuantity(original.getQuantity());

    return result;
  }

  public static Predicate<String, OrderMessage> isLargeQuantity() {
    return (key, value) -> value.getQuantity() > 200;
  }

  public static Predicate<? super String, ? super OrderPatternMessage> isPlastic() {
    return (key, value) -> StringUtils.startsWithIgnoreCase(value.getItemName(), "Plastic");
  }

  public static Predicate<? super String,? super OrderMessage> isCheap() {
    return (key, value) -> value.getPrice() < 100;
  }

  public static KeyValueMapper<String, OrderMessage, String> generateStorageKey() {
    return(key, value) -> Base64.getEncoder().encodeToString(value.getOrderNumber().getBytes());
  }

  public static KeyValueMapper<String, OrderMessage, KeyValue<String, OrderRewardMessage>> mapToOrderRewardChangeKey() {
    return (key, value) -> KeyValue.pair(value.getOrderLocation(), mapToOrderReward(value));
  }
}
