package com.course.kafka.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id @GeneratedValue private int orderItemId;

  @Column(nullable = false, length = 200)
  private String itemName;

  @Column(nullable = false)
  private int price;

  @Column(nullable = false)
  private int quantity;

  @Override
  public String toString() {
    return "OrderItem{" +
            "orderItemId=" + orderItemId +
            ", itemName='" + itemName + '\'' +
            ", price=" + price +
            ", quantity=" + quantity +
            ", order=" + order +
            '}';
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  @JoinColumn(name = "order_id")
  @ManyToOne
  private Order order;

  public int getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(int orderItemId) {
    this.orderItemId = orderItemId;
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
}
