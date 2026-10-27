package cn.xrj.fruitshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.bean.OrderBean;
import cn.xrj.fruitshop.bean.OrderItemBean;

/**
 * 订单数据访问对象，下单时 orders 和 order_item 一起写，放在同一个事务里。
 */
public class OrderDao {

    private final DbHelper helper;

    public OrderDao(Context context) {
        helper = DbHelper.getInstance(context);
    }

    /** 写入订单和明细，返回订单主键 */
    public long insertOrder(OrderBean order) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long orderId;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("order_no", order.getOrderNo());
            values.put("shop_name", order.getShopName());
            values.put("total_price", order.getTotalPrice());
            values.put("delivery_fee", order.getDeliveryFee());
            values.put("address", order.getAddress());
            values.put("phone", order.getPhone());
            values.put("status", order.getStatus());
            values.put("create_time", order.getCreateTime());
            orderId = db.insert(DbHelper.TABLE_ORDER, null, values);

            if (order.getItems() != null) {
                for (OrderItemBean item : order.getItems()) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put("order_id", orderId);
                    itemValues.put("food_name", item.getFoodName());
                    itemValues.put("price", item.getPrice());
                    itemValues.put("count", item.getCount());
                    db.insert(DbHelper.TABLE_ORDER_ITEM, null, itemValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return orderId;
    }

    /** 更新订单状态，1 待付款 2 已支付 3 已完成 */
    public void updateStatus(int orderId, int status) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update(DbHelper.TABLE_ORDER, values, "id=?", new String[]{String.valueOf(orderId)});
    }

    /** 全部订单，按下单时间倒序 */
    public List<OrderBean> listAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<OrderBean> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_ORDER, null, null, null, null, null, "id DESC");
        try {
            while (cursor.moveToNext()) {
                OrderBean bean = new OrderBean();
                bean.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                bean.setOrderNo(cursor.getString(cursor.getColumnIndexOrThrow("order_no")));
                bean.setShopName(cursor.getString(cursor.getColumnIndexOrThrow("shop_name")));
                bean.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
                bean.setDeliveryFee(cursor.getDouble(cursor.getColumnIndexOrThrow("delivery_fee")));
                bean.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                bean.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                bean.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
                bean.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("create_time")));
                list.add(bean);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /** 某笔订单的明细 */
    public List<OrderItemBean> listItems(int orderId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<OrderItemBean> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_ORDER_ITEM, null, "order_id=?",
                new String[]{String.valueOf(orderId)}, null, null, "id ASC");
        try {
            while (cursor.moveToNext()) {
                OrderItemBean bean = new OrderItemBean();
                bean.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                bean.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
                bean.setFoodName(cursor.getString(cursor.getColumnIndexOrThrow("food_name")));
                bean.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                bean.setCount(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
                list.add(bean);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
