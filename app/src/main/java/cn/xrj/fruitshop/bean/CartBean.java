package cn.xrj.fruitshop.bean;

import java.io.Serializable;

/**
 * 购物车条目，对应 cart 表。
 */
public class CartBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int shopId;
    private int foodId;
    private String foodName;
    private double price;
    private int count;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /** 单行小计 */
    public double getSubtotal() {
        return price * count;
    }
}
