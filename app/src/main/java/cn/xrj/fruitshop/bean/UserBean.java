package cn.xrj.fruitshop.bean;

import java.io.Serializable;

/**
 * 用户实体，注册信息以 JSON 数组的形式存在 SharedPreferences 的 users 键下。
 */
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String phone;
    private String nickname;
    private String password;

    public UserBean() {
    }

    public UserBean(String phone, String nickname, String password) {
        this.phone = phone;
        this.nickname = nickname;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
