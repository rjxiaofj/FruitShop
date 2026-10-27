package cn.xrj.fruitshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.bean.FoodBean;

/**
 * 购物车数据访问对象，SQL 全部走 ContentValues 和占位符，不拼字符串。
 */
public class CartDao {

    private final DbHelper helper;

    public CartDao(Context context) {
        helper = DbHelper.getInstance(context);
    }

    /** 加购，同一店铺同一商品数量加一，没有就新增一行 */
    public void add(int shopId, FoodBean food) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int current = countOf(db, shopId, food.getFoodId());
        ContentValues values = new ContentValues();
        if (current > 0) {
            values.put("count", current + 1);
            db.update(DbHelper.TABLE_CART, values,
                    "shop_id=? AND food_id=?",
                    new String[]{String.valueOf(shopId), String.valueOf(food.getFoodId())});
        } else {
            values.put("shop_id", shopId);
            values.put("food_id", food.getFoodId());
            values.put("food_name", food.getFoodName());
            values.put("price", food.getPrice());
            values.put("count", 1);
            values.put("image", food.getImage());
            db.insert(DbHelper.TABLE_CART, null, values);
        }
    }

    /** 减购，数量减到零就删掉这一行 */
    public void minus(int shopId, int foodId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int current = countOf(db, shopId, foodId);
        if (current <= 0) {
            return;
        }
        if (current == 1) {
            delete(shopId, foodId);
        } else {
            ContentValues values = new ContentValues();
            values.put("count", current - 1);
            db.update(DbHelper.TABLE_CART, values,
                    "shop_id=? AND food_id=?",
                    new String[]{String.valueOf(shopId), String.valueOf(foodId)});
        }
    }

    public void delete(int shopId, int foodId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbHelper.TABLE_CART, "shop_id=? AND food_id=?",
                new String[]{String.valueOf(shopId), String.valueOf(foodId)});
    }

    public void deleteById(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbHelper.TABLE_CART, "id=?", new String[]{String.valueOf(id)});
    }

    /** 清空某家店的购物车 */
    public void clearByShop(int shopId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbHelper.TABLE_CART, "shop_id=?", new String[]{String.valueOf(shopId)});
    }

    /** 清空全部购物车，下单成功后调用 */
    public void clearAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbHelper.TABLE_CART, null, null);
    }

    public List<CartBean> listByShop(int shopId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<CartBean> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_CART, null, "shop_id=?",
                new String[]{String.valueOf(shopId)}, null, null, "id ASC");
        try {
            while (cursor.moveToNext()) {
                list.add(readCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /** 该店购物车总件数 */
    public int countByShop(int shopId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(count) FROM " + DbHelper.TABLE_CART + " WHERE shop_id=?",
                new String[]{String.valueOf(shopId)});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    /** 全部店铺的购物车件数，底部导航角标用 */
    public int countAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(count) FROM " + DbHelper.TABLE_CART, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    /** 该店购物车总金额 */
    public double totalPriceByShop(int shopId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(price*count) FROM " + DbHelper.TABLE_CART + " WHERE shop_id=?",
                new String[]{String.valueOf(shopId)});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        } finally {
            cursor.close();
        }
        return 0d;
    }

    private int countOf(SQLiteDatabase db, int shopId, int foodId) {
        Cursor cursor = db.query(DbHelper.TABLE_CART, new String[]{"count"},
                "shop_id=? AND food_id=?",
                new String[]{String.valueOf(shopId), String.valueOf(foodId)},
                null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    private CartBean readCursor(Cursor cursor) {
        CartBean bean = new CartBean();
        bean.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        bean.setShopId(cursor.getInt(cursor.getColumnIndexOrThrow("shop_id")));
        bean.setFoodId(cursor.getInt(cursor.getColumnIndexOrThrow("food_id")));
        bean.setFoodName(cursor.getString(cursor.getColumnIndexOrThrow("food_name")));
        bean.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        bean.setCount(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
        bean.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
        return bean;
    }
}
