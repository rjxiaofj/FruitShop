package cn.xrj.fruitshop.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.fragment.CartFragment;
import cn.xrj.fruitshop.fragment.HomeFragment;
import cn.xrj.fruitshop.fragment.UserFragment;

/**
 * 主页容器，底部导航切换三个 Fragment。
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "home";
    private static final String TAG_CART = "cart";
    private static final String TAG_USER = "user";

    private TextView tvTitle;
    private BottomNavigationView navBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tv_title);
        navBottom = findViewById(R.id.nav_bottom);

        // 首页不显示返回按钮
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setVisibility(View.GONE);

        navBottom.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.tab_cart) {
                    switchTo(TAG_CART);
                    tvTitle.setText(R.string.tab_cart);
                } else if (id == R.id.tab_user) {
                    switchTo(TAG_USER);
                    tvTitle.setText(R.string.tab_user);
                } else {
                    switchTo(TAG_HOME);
                    tvTitle.setText(R.string.title_home);
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            switchTo(TAG_HOME);
            navBottom.setSelectedItemId(R.id.tab_home);
        }
    }

    /** 供 Fragment 调用，切换底部导航 */
    public void selectTab(int itemId) {
        navBottom.setSelectedItemId(itemId);
    }

    private void switchTo(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment target = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();

        for (Fragment fragment : manager.getFragments()) {
            if (fragment != target && !fragment.isHidden()) {
                transaction.hide(fragment);
            }
        }

        if (target == null) {
            transaction.add(R.id.fl_container, createFragment(tag), tag);
        } else {
            transaction.show(target);
        }
        transaction.commit();
    }

    private Fragment createFragment(String tag) {
        if (TAG_CART.equals(tag)) {
            return new CartFragment();
        }
        if (TAG_USER.equals(tag)) {
            return new UserFragment();
        }
        return new HomeFragment();
    }
}
