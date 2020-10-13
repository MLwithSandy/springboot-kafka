package com.course.kafkaconsumer.entity;

public class Image {
  private String name;
  private long size;
  private String type;

  public Image(String name, long size, String type) {
    this.name = name;
    this.size = size;
    this.type = type;
  }

  @Override
  public String toString() {
    return "Image{" + "name='" + name + '\'' + ", size=" + size + ", type='" + type + '\'' + '}';
  }

  public Image() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
