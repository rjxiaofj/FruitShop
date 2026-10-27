package cn.xrj.fruitshop.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类，库名 fruitshop.db，包含购物车、订单、订单明细三张表。
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fruitshop.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDER = "orders";
    public static final String TABLE_ORDER_ITEM = "order_item";

    private static final String SQL_CREATE_CART =
            "CREATE TABLE " + TABLE_CART + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "shop_id INTEGER NOT NULL,"
                    + "food_id INTEGER NOT NULL,"
                    + "food_name TEXT NOT NULL,"
                    + "price REAL NOT NULL,"
                    + "count INTEGER NOT NULL DEFAULT 1,"
                    + "image TEXT)";

    private static final String SQL_CREATE_ORDER =
            "CREATE TABLE " + TABLE_ORDER + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "order_no TEXT NOT NULL,"
                    + "shop_name TEXT,"
                    + "total_price REAL NOT NULL,"
                    + "delivery_fee REAL DEFAULT 0,"
                    + "address TEXT,"
                    + "phone TEXT,"
                    + "status INTEGER DEFAULT 1,"
                    + "create_time TEXT)";

    private static final String SQL_CREATE_ORDER_ITEM =
            "CREATE TABLE " + TABLE_ORDER_ITEM + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "order_id INTEGER NOT NULL,"
                    + "food_name TEXT NOT NULL,"
                    + "price REAL NOT NULL,"
                    + "count INTEGER NOT NULL)";

    private static volatile DbHelper instance;

    private DbHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    instance = new DbHelper(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CART);
        db.execSQL(SQL_CREATE_ORDER);
        db.execSQL(SQL_CREATE_ORDER_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEM);
        onCreate(db);
    }
}
