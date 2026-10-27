package cn.xrj.fruitshop.bean;

import java.io.Serializable;

/**
 * 订单明细，对应 order_item 表。
 */
public class OrderItemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int orderId;
    private String foodName;
    private double price;
    private int count;

    public OrderItemBean() {
    }

    public OrderItemBean(String foodName, double price, int count) {
        this.foodName = foodName;
        this.price = price;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getSubtotal() {
        return price * count;
    }
}
