package com.course.kafkaproducer.entity;

public class FoodOrder {
  private String item;
  private int amount;

  public FoodOrder(String item, int amount) {
    this.item = item;
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "FoodOrder{" + "item='" + item + '\'' + ", amount=" + amount + '}';
  }

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }
}
