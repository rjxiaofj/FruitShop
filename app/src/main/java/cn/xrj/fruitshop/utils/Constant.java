package cn.xrj.fruitshop.utils;

import java.util.Locale;

/**
 * 全局常量与地址配置。
 * 本项目数据放在 assets 里，不依赖服务器，断网也能演示。
 */
public class Constant {

    /** 店铺与商品数据文件，位于 app/src/main/assets/ */
    public static final String SHOP_JSON = "shop_list_data.json";

    /** assets 目录对应的 URI 前缀，Picasso 可以直接加载 */
    public static final String ASSET_BASE = "file:///android_asset/";

    /** 页面跳转时传递实体的键名 */
    public static final String EXTRA_SHOP = "shop";
    public static final String EXTRA_FOOD = "food";
    public static final String EXTRA_SHOP_ID = "shop_id";
    public static final String EXTRA_SHOP_NAME = "shop_name";
    public static final String EXTRA_DELIVERY_FEE = "delivery_fee";
    public static final String EXTRA_CART_LIST = "cart_list";

    /** 首页轮播广告图 */
    public static final String[] BANNERS = {
            ASSET_BASE + "img/banner/1.jpg",
            ASSET_BASE + "img/banner/2.jpg",
            ASSET_BASE + "img/banner/3.jpg"
    };

    /** 统一价格格式，保留两位小数 */
    public static String money(double value) {
        return String.format(Locale.CHINA, "%.2f", value);
    }
}
