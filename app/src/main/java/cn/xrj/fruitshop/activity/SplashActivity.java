package cn.xrj.fruitshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.utils.UserManager;

/**
 * 启动页，停留 3 秒后按登录状态跳到主页或登录页。
 */
public class SplashActivity extends AppCompatActivity {

    private static final long DELAY = 3000L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable jump = new Runnable() {
        @Override
        public void run() {
            boolean login = UserManager.getInstance(SplashActivity.this).isLogin();
            startActivity(new Intent(SplashActivity.this,
                    login ? MainActivity.class : LoginActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(jump, DELAY);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(jump);
        super.onDestroy();
    }
}
