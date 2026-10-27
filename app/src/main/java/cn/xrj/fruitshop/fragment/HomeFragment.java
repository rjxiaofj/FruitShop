package cn.xrj.fruitshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.activity.ShopDetailActivity;
import cn.xrj.fruitshop.adapter.BannerAdapter;
import cn.xrj.fruitshop.adapter.ShopAdapter;
import cn.xrj.fruitshop.bean.ShopBean;
import cn.xrj.fruitshop.utils.Constant;
import cn.xrj.fruitshop.utils.JsonParse;

/**
 * 首页：顶部轮播广告加店铺列表。
 * 数据在子线程读 assets，回到主线程刷新界面，加载中、失败、空数据三种状态都有对应视图。
 */
public class HomeFragment extends Fragment {

    private static final long BANNER_INTERVAL = 3500L;

    private ViewPager2 vpBanner;
    private LinearLayout llDots;
    private RecyclerView rvShop;
    private ProgressBar pbLoading;
    private LinearLayout llError;
    private LinearLayout llEmpty;
    private TextView tvError;

    private final List<ShopBean> shops = new ArrayList<>();
    private ShopAdapter shopAdapter;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executor;

    private final Runnable bannerTask = new Runnable() {
        @Override
        public void run() {
            if (vpBanner == null) {
                return;
            }
            int next = (vpBanner.getCurrentItem() + 1) % Constant.BANNERS.length;
            vpBanner.setCurrentItem(next, true);
            mainHandler.postDelayed(this, BANNER_INTERVAL);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupBanner();
        setupShopList();
        loadShops();
        mainHandler.postDelayed(bannerTask, BANNER_INTERVAL);
    }

    private void initViews(View view) {
        executor = Executors.newSingleThreadExecutor();
        vpBanner = view.findViewById(R.id.vp_banner);
        llDots = view.findViewById(R.id.ll_dots);
        rvShop = view.findViewById(R.id.rv_shop);
        pbLoading = view.findViewById(R.id.pb_loading);
        llError = view.findViewById(R.id.ll_error);
        llEmpty = view.findViewById(R.id.ll_empty);
        tvError = view.findViewById(R.id.tv_error);

        view.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadShops();
            }
        });
    }

    private void setupBanner() {
        vpBanner.setAdapter(new BannerAdapter(Constant.BANNERS));
        buildDots(0);
        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                buildDots(position);
            }
        });
    }

    private void buildDots(int selected) {
        llDots.removeAllViews();
        int size = dp(6);
        for (int i = 0; i < Constant.BANNERS.length; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(dp(3), 0, dp(3), 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(i == selected ? R.drawable.dot_selected : R.drawable.dot_normal);
            llDots.addView(dot);
        }
    }

    private void setupShopList() {
        shopAdapter = new ShopAdapter(shops, new ShopAdapter.OnShopClickListener() {
            @Override
            public void onShopClick(ShopBean shop) {
                Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                intent.putExtra(Constant.EXTRA_SHOP, shop);
                startActivity(intent);
            }
        });
        rvShop.setLayoutManager(new LinearLayoutManager(getContext()));
        rvShop.setAdapter(shopAdapter);
    }

    private void loadShops() {
        showLoading();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<ShopBean> list = JsonParse.loadShopList(requireContext().getApplicationContext());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) {
                                return;
                            }
                            shops.clear();
                            shops.addAll(list);
                            shopAdapter.notifyDataSetChanged();
                            if (shops.isEmpty()) {
                                showEmpty();
                            } else {
                                showContent();
                            }
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) {
                                return;
                            }
                            showError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        rvShop.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
    }

    private void showContent() {
        pbLoading.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        rvShop.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        pbLoading.setVisibility(View.GONE);
        rvShop.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        pbLoading.setVisibility(View.GONE);
        rvShop.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
        String text = (message == null || message.trim().isEmpty())
                ? getString(R.string.load_failed) : message;
        tvError.setText(text);
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private int dp(int value) {
        return (int) (getResources().getDisplayMetrics().density * value + 0.5f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainHandler.removeCallbacks(bannerTask);
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
