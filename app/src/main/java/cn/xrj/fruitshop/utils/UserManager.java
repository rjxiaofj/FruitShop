package cn.xrj.fruitshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.bean.UserBean;

/**
 * 登录状态与注册用户的管理类。
 * SharedPreferences 文件名 user，users 键存注册用户的 JSON 数组，login_phone 键存当前登录手机号。
 */
public class UserManager {

    private static final String SP_NAME = "user";
    private static final String KEY_USERS = "users";
    private static final String KEY_LOGIN_PHONE = "login_phone";

    private static volatile UserManager instance;

    private final SharedPreferences sp;
    private final Gson gson = new Gson();

    private UserManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager(context);
                }
            }
        }
        return instance;
    }

    /** 是否已登录 */
    public boolean isLogin() {
        return !TextUtils.isEmpty(getLoginPhone());
    }

    public String getLoginPhone() {
        return sp.getString(KEY_LOGIN_PHONE, "");
    }

    /** 取当前登录用户，未登录或找不到返回 null */
    public UserBean getLoginUser() {
        String phone = getLoginPhone();
        if (TextUtils.isEmpty(phone)) {
            return null;
        }
        for (UserBean user : getUsers()) {
            if (phone.equals(user.getPhone())) {
                return user;
            }
        }
        return null;
    }

    /** 读全部注册用户 */
    public List<UserBean> getUsers() {
        String json = sp.getString(KEY_USERS, "");
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<UserBean>>() {
        }.getType();
        List<UserBean> list = gson.fromJson(json, type);
        return list == null ? new ArrayList<UserBean>() : list;
    }

    /** 手机号是否已注册 */
    public boolean isPhoneExist(String phone) {
        for (UserBean user : getUsers()) {
            if (phone.equals(user.getPhone())) {
                return true;
            }
        }
        return false;
    }

    /** 注册，写回 users 键，成功后直接置为登录态 */
    public boolean register(UserBean user) {
        if (isPhoneExist(user.getPhone())) {
            return false;
        }
        List<UserBean> users = getUsers();
        users.add(user);
        sp.edit()
                .putString(KEY_USERS, gson.toJson(users))
                .putString(KEY_LOGIN_PHONE, user.getPhone())
                .apply();
        return true;
    }

    /** 登录校验，手机号密码都匹配才写入 login_phone */
    public boolean login(String phone, String password) {
        for (UserBean user : getUsers()) {
            if (phone.equals(user.getPhone()) && password.equals(user.getPassword())) {
                sp.edit().putString(KEY_LOGIN_PHONE, phone).apply();
                return true;
            }
        }
        return false;
    }

    /** 退出登录，只清 login_phone，注册信息保留 */
    public void logout() {
        sp.edit().remove(KEY_LOGIN_PHONE).apply();
    }
}
