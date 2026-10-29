package cn.xrj.fruitshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.activity.ShopDetailActivity;
import cn.xrj.fruitshop.adapter.HomeEmptyAdapter;
import cn.xrj.fruitshop.adapter.HomeHeaderAdapter;
import cn.xrj.fruitshop.adapter.ShopAdapter;
import cn.xrj.fruitshop.bean.FoodBean;
import cn.xrj.fruitshop.bean.ShopBean;
import cn.xrj.fruitshop.utils.Constant;
import cn.xrj.fruitshop.utils.JsonParse;

/**
 * 首页：搜索栏和轮播是列表头部，跟店铺列表一起滚动。
 * 数据在子线程读 assets，回到主线程刷新界面，加载中和失败有对应视图。
 */
public class HomeFragment extends Fragment implements HomeHeaderAdapter.OnSearchListener {

    private final List<ShopBean> shops = new ArrayList<>();
    private final List<ShopBean> allShops = new ArrayList<>();

    private HomeHeaderAdapter headerAdapter;
    private ShopAdapter shopAdapter;
    private HomeEmptyAdapter emptyAdapter;

    private RecyclerView rvHome;
    private ProgressBar pbLoading;
    private LinearLayout llError;
    private TextView tvError;
    private String keyword = "";

    private ExecutorService executor;

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
        setupList();
        loadShops();
    }

    private void initViews(View view) {
        executor = Executors.newSingleThreadExecutor();
        rvHome = view.findViewById(R.id.rv_home);
        pbLoading = view.findViewById(R.id.pb_loading);
        llError = view.findViewById(R.id.ll_error);
        tvError = view.findViewById(R.id.tv_error);

        view.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadShops();
            }
        });
    }

    private void setupList() {
        headerAdapter = new HomeHeaderAdapter(Constant.BANNERS, this);
        shopAdapter = new ShopAdapter(shops, new ShopAdapter.OnShopClickListener() {
            @Override
            public void onShopClick(ShopBean shop) {
                Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                intent.putExtra(Constant.EXTRA_SHOP, shop);
                startActivity(intent);
            }
        });
        emptyAdapter = new HomeEmptyAdapter();

        ConcatAdapter concat = new ConcatAdapter(headerAdapter, shopAdapter, emptyAdapter);
        rvHome.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHome.setAdapter(concat);

        headerAdapter.startBanner();
    }

    @Override
    public void onSearch(String keyword) {
        this.keyword = keyword;
        applyFilter();
    }

    /** 按关键词过滤，空关键词恢复全部 */
    private void applyFilter() {
        shops.clear();
        if (keyword.isEmpty()) {
            shops.addAll(allShops);
        } else {
            for (ShopBean shop : allShops) {
                if (matches(shop, keyword)) {
                    shops.add(shop);
                }
            }
        }
        shopAdapter.notifyDataSetChanged();

        if (shops.isEmpty()) {
            emptyAdapter.setText(keyword.isEmpty()
                    ? getString(R.string.empty_data) : getString(R.string.search_empty));
        } else {
            emptyAdapter.setText("");
        }
    }

    /** 店名或任一商品名包含关键词即命中 */
    private boolean matches(ShopBean shop, String key) {
        if (shop.getShopName() != null && shop.getShopName().contains(key)) {
            return true;
        }
        if (shop.getFoods() != null) {
            for (FoodBean food : shop.getFoods()) {
                if (food.getFoodName() != null && food.getFoodName().contains(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadShops() {
        showLoading();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<ShopBean> list = JsonParse.loadShopList(requireContext().getApplicationContext());
                    mainHandlerPost(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) {
                                return;
                            }
                            shops.clear();
                            allShops.clear();
                            allShops.addAll(list);
                            applyFilter();
                            showContent();
                        }
                    });
                } catch (final Exception e) {
                    mainHandlerPost(new Runnable() {
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

    private void mainHandlerPost(Runnable action) {
        if (getActivity() == null) {
            return;
        }
        new android.os.Handler(android.os.Looper.getMainLooper()).post(action);
    }

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        llError.setVisibility(View.GONE);
    }

    private void showContent() {
        pbLoading.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        pbLoading.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
        String text = (message == null || message.trim().isEmpty())
                ? getString(R.string.load_failed) : message;
        tvError.setText(text);
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (headerAdapter != null) {
            headerAdapter.stopBanner();
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
