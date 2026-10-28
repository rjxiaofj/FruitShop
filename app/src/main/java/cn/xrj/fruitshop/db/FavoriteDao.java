package cn.xrj.fruitshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.bean.FoodBean;

/**
 * 收藏数据访问对象，一个商品在 favorite 表里最多一条记录。
 */
public class FavoriteDao {

    private final DbHelper helper;

    public FavoriteDao(Context context) {
        helper = DbHelper.getInstance(context);
    }

    /** 该商品是否已收藏 */
    public boolean isFavorite(int foodId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_FAVORITE, new String[]{"id"},
                "food_id=?", new String[]{String.valueOf(foodId)}, null, null, null);
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    /** 收藏，重复收藏不会写入第二条 */
    public void add(int shopId, FoodBean food) {
        if (isFavorite(food.getFoodId())) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("shop_id", shopId);
        values.put("food_id", food.getFoodId());
        values.put("food_name", food.getFoodName());
        values.put("image", food.getImage());
        db.insert(DbHelper.TABLE_FAVORITE, null, values);
    }

    /** 取消收藏 */
    public void remove(int foodId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DbHelper.TABLE_FAVORITE, "food_id=?", new String[]{String.valueOf(foodId)});
    }

    /** 切换收藏状态，返回切换后是否已收藏 */
    public boolean toggle(int shopId, FoodBean food) {
        if (isFavorite(food.getFoodId())) {
            remove(food.getFoodId());
            return false;
        }
        add(shopId, food);
        return true;
    }

    /** 全部收藏的商品 id */
    public List<Integer> listFoodIds() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Integer> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_FAVORITE, new String[]{"food_id"},
                null, null, null, null, "id DESC");
        try {
            while (cursor.moveToNext()) {
                list.add(cursor.getInt(cursor.getColumnIndexOrThrow("food_id")));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /** 收藏数量 */
    public int count() {
        return listFoodIds().size();
    }
}
