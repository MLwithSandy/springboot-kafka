package com.course.kafkaconsumer.entity;

public class Commodity {
    private String name;
    private double price;
    private String measurement;
    private long timestamp;

    public Commodity(String name, double price, String measurement, long timestamp) {
        super();
        this.name = name;
        setPrice(price);
        this.measurement = measurement;
        this.timestamp = timestamp;
    }

    public Commodity() {
    }

    @Override
    public String toString() {
        return "Commodity ["
                + "name='"
                + name
                + '\''
                + ", price="
                + price
                + ", measurement='"
                + measurement
                + '\''
                + ", timestamp="
                + timestamp
                + ']';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        // rounding to 2 decimal digits
        this.price = Math.round(price * 100d) / 100d;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
