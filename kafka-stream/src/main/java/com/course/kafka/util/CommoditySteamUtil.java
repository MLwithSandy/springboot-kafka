package com.course.kafka.util;

import com.course.kafka.broker.message.OrderMessage;
import com.course.kafka.broker.message.OrderPatternMessage;
import com.course.kafka.broker.message.OrderRewardMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.Predicate;

public class CommoditySteamUtil {

  public static OrderMessage maskCreditCard(OrderMessage original) {
    var masked = original.copy();
    masked.setCreditCardNumber(
        original.getCreditCardNumber().replaceFirst("\\d{12}", StringUtils.repeat('*', 12)));
    return masked;
  }

  public static OrderPatternMessage mapToOrderPattern(OrderMessage original){
    var result = new OrderPatternMessage();
    result.setItemName(original.getItemName());
    result.setOrderDateTime(original.getOrderDateTime());
    result.setOrderLocation(original.getOrderLocation());
    result.setOrderNumber(original.getOrderNumber());

    var totalItemAmount = original.getQuantity() * original.getPrice();
    result.setTotalItemAmount(totalItemAmount);

    return result;
  }

  public static OrderRewardMessage mapToOrderReward(OrderMessage original){
    var result = new OrderRewardMessage();

    result.setItemName(original.getItemName());
    result.setOrderDateTime(original.getOrderDateTime());
    result.setOrderLocation(original.getOrderLocation());
    result.setOrderNumber(original.getOrderNumber());
    result.setPrice(original.getPrice());
    result.setQuantity(original.getQuantity());

    return result;
  }

  public static Predicate<String, OrderMessage> isLargeQuantity(){
    return (key, value) -> value.getQuantity() > 200;
  }
}
