package cn.xrj.fruitshop.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.xrj.fruitshop.bean.ShopBean;

/**
 * 读取 assets 里的 JSON 并解析成实体集合。
 * 读取和解析都放在子线程里调用，失败时抛出异常由页面捕获提示。
 */
public class JsonParse {

    /** 从 assets 读出文本 */
    public static String readAsset(Context context, String fileName) throws IOException {
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /** 读取并解析店铺列表 */
    public static List<ShopBean> loadShopList(Context context) throws IOException, JsonParseException {
        String json = readAsset(context, Constant.SHOP_JSON);
        return parseShopList(json);
    }

    /** 解析 JSON 文本，失败抛 JsonParseException */
    public static List<ShopBean> parseShopList(String json) throws JsonParseException {
        if (json == null || json.trim().isEmpty()) {
            throw new JsonParseException("数据为空");
        }
        Type type = new TypeToken<List<ShopBean>>() {
        }.getType();
        List<ShopBean> list = new Gson().fromJson(json, type);
        if (list == null || list.isEmpty()) {
            throw new JsonParseException("没有解析到店铺数据");
        }
        return list;
    }
}
