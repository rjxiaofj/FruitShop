package cn.xrj.fruitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.utils.UserManager;

/**
 * 登录页，手机号加密码登录，登录成功进入主页。
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etPassword;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 浅色背景配深色状态栏图标
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        userManager = UserManager.getInstance(this);

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void doLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isPhoneValid(phone)) {
            Toast.makeText(this, R.string.tips_phone_format, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, R.string.tips_password_length, Toast.LENGTH_SHORT).show();
            return;
        }

        if (userManager.login(phone, password)) {
            Toast.makeText(this, R.string.tips_login_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.tips_login_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /** 手机号 11 位且以 1 开头 */
    static boolean isPhoneValid(String phone) {
        return !TextUtils.isEmpty(phone) && phone.matches("1\\d{10}");
    }
}
