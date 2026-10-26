package cn.xrj.fruitshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cn.xrj.fruitshop.R;

/**
 * 主页容器，后续承载底部导航与三个 Fragment。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.title_home);

        // 首页不显示返回按钮
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setVisibility(View.GONE);
    }
}
