package cn.xrj.fruitshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.activity.MainActivity;
import cn.xrj.fruitshop.activity.OrderActivity;
import cn.xrj.fruitshop.adapter.CartPageAdapter;
import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.bean.FoodBean;
import cn.xrj.fruitshop.bean.ShopBean;
import cn.xrj.fruitshop.db.CartDao;
import cn.xrj.fruitshop.utils.Constant;
import cn.xrj.fruitshop.utils.JsonParse;

/**
 * 购物车页：从 SQLite 读出全部条目，按店铺分组展示，支持加减删除和分店结算。
 */
public class CartFragment extends Fragment implements CartPageAdapter.OnCartPageListener {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, ShopBean> shopMap = new LinkedHashMap<>();
    private final List<Object> rows = new ArrayList<>();

    private CartDao cartDao;
    private CartPageAdapter adapter;
    private RecyclerView rvCart;
    private View layoutEmpty;
    private ExecutorService executor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartDao = new CartDao(requireContext());
        executor = Executors.newSingleThreadExecutor();

        rvCart = view.findViewById(R.id.rv_cart);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CartPageAdapter(this);
        rvCart.setAdapter(adapter);

        view.findViewById(R.id.btn_go_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).selectTab(R.id.tab_home);
                }
            }
        });

        loadShops();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        }
    }

    /** 子线程读店铺数据，拿店名和配送费 */
    private void loadShops() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<ShopBean> list = null;
                try {
                    list = JsonParse.loadShopList(requireContext());
                } catch (Exception ignored) {
                }
                final List<ShopBean> result = list;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAdded()) {
                            return;
                        }
                        shopMap.clear();
                        if (result != null) {
                            for (ShopBean shop : result) {
                                shopMap.put(shop.getShopId(), shop);
                            }
                        }
                        refresh();
                    }
                });
            }
        });
    }

    /** 重新读库并按店铺分组 */
    private void refresh() {
        if (cartDao == null || adapter == null || layoutEmpty == null) {
            return;
        }
        List<CartBean> all = cartDao.listAll();

        rows.clear();
        Map<Integer, CartPageAdapter.ShopSection> sections = new LinkedHashMap<>();
        for (CartBean item : all) {
            CartPageAdapter.ShopSection section = sections.get(item.getShopId());
            if (section == null) {
                ShopBean shop = shopMap.get(item.getShopId());
                String name = shop != null ? shop.getShopName()
                        : getString(R.string.shop_fallback, item.getShopId());
                double fee = shop != null ? shop.getDeliveryFee() : 0d;
                section = new CartPageAdapter.ShopSection(item.getShopId(), name, fee);
                sections.put(item.getShopId(), section);
                rows.add(section);
            }
            section.items.add(item);
            rows.add(item);
        }
        adapter.setRows(rows);

        boolean empty = all.isEmpty();
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvCart.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPlus(CartBean item) {
        cartDao.add(item.getShopId(), toFood(item));
        refresh();
    }

    @Override
    public void onMinus(CartBean item) {
        cartDao.minus(item.getShopId(), item.getFoodId());
        refresh();
    }

    @Override
    public void onDelete(CartBean item) {
        cartDao.delete(item.getShopId(), item.getFoodId());
        refresh();
    }

    /** 一家店单独结算，把该店购物车传给订单页 */
    @Override
    public void onSettle(CartPageAdapter.ShopSection section) {
        List<CartBean> list = cartDao.listByShop(section.shopId);
        if (list.isEmpty()) {
            Toast.makeText(requireContext(), R.string.cart_is_empty, Toast.LENGTH_SHORT).show();
            refresh();
            return;
        }
        Intent intent = new Intent(requireContext(), OrderActivity.class);
        intent.putExtra(Constant.EXTRA_CART_LIST, new ArrayList<>(list));
        intent.putExtra(Constant.EXTRA_SHOP_NAME, section.shopName);
        intent.putExtra(Constant.EXTRA_SHOP_ID, section.shopId);
        intent.putExtra(Constant.EXTRA_DELIVERY_FEE, section.deliveryFee);
        startActivity(intent);
    }

    private FoodBean toFood(CartBean item) {
        FoodBean food = new FoodBean();
        food.setFoodId(item.getFoodId());
        food.setFoodName(item.getFoodName());
        food.setPrice(item.getPrice());
        food.setImage(item.getImage());
        return food;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainHandler.removeCallbacksAndMessages(null);
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
