package cn.xrj.fruitshop.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 订单实体，对应 orders 表。
 */
public class OrderBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 1 待付款 2 已支付 3 已完成 */
    public static final int STATUS_UNPAID = 1;
    public static final int STATUS_PAID = 2;
    public static final int STATUS_DONE = 3;

    private int id;
    private String orderNo;
    private String shopName;
    private double totalPrice;
    private double deliveryFee;
    private String address;
    private String phone;
    private int status = STATUS_UNPAID;
    private String createTime;
    private List<OrderItemBean> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderItemBean> getItems() {
        return items;
    }

    public void setItems(List<OrderItemBean> items) {
        this.items = items;
    }
}
