package cn.xrj.fruitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.UserBean;
import cn.xrj.fruitshop.utils.UserManager;

/**
 * 注册页，校验手机号格式、密码长度、两次密码一致，注册成功后直接进入主页。
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etPassword;
    private EditText etPassword2;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = UserManager.getInstance(this);

        ((android.widget.TextView) findViewById(R.id.tv_title)).setText(R.string.title_register);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etPassword2 = findViewById(R.id.et_password2);

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });

        findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void doRegister() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String password2 = etPassword2.getText().toString().trim();

        if (!LoginActivity.isPhoneValid(phone)) {
            Toast.makeText(this, R.string.tips_phone_format, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            Toast.makeText(this, R.string.tips_password_length, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, R.string.tips_password_diff, Toast.LENGTH_SHORT).show();
            return;
        }
        if (userManager.isPhoneExist(phone)) {
            Toast.makeText(this, R.string.tips_phone_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        UserBean user = new UserBean(phone, getString(R.string.nickname_prefix,
                phone.substring(phone.length() - 4)), password);
        if (userManager.register(user)) {
            Toast.makeText(this, R.string.tips_register_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
