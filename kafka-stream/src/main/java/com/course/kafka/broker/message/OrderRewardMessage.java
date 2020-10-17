package com.course.kafka.broker.message;

import com.course.kafka.util.LocalDateTimeDeserializer;
import com.course.kafka.util.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public class OrderRewardMessage {

  private String orderNumber;

  private String orderLocation;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime orderDateTime;

  private String itemName;

  private int price;

  private int quantity;

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public String getOrderLocation() {
    return orderLocation;
  }

  public void setOrderLocation(String orderLocation) {
    this.orderLocation = orderLocation;
  }

  public LocalDateTime getOrderDateTime() {
    return orderDateTime;
  }

  public void setOrderDateTime(LocalDateTime orderDateTime) {
    this.orderDateTime = orderDateTime;
  }



  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  @Override
  public String toString() {
    return "OrderMessage{"
        + "orderNumber='"
        + orderNumber
        + '\''
        + ", orderLocation='"
        + orderLocation
        + '\''
        + ", orderDateTime="
        + orderDateTime
        + '\''
        + ", itemName='"
        + itemName
        + '\''
        + ", price="
        + price
        + ", quantity="
        + quantity
        + '}';
  }
}
