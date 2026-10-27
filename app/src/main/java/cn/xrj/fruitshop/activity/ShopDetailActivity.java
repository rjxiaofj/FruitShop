package cn.xrj.fruitshop.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.adapter.CarAdapter;
import cn.xrj.fruitshop.adapter.MenuAdapter;
import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.bean.FoodBean;
import cn.xrj.fruitshop.bean.ShopBean;
import cn.xrj.fruitshop.db.CartDao;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 店铺详情页：店铺信息、菜单列表、底部购物车栏。
 * 加购减购都写 SQLite 的 cart 表，每次变动后重新统计件数和总价。
 */
public class ShopDetailActivity extends AppCompatActivity {

    private ShopBean shop;
    private CartDao cartDao;

    private TextView tvBadge;
    private TextView tvTotal;
    private TextView tvFee;

    private CarAdapter carAdapter;
    private Dialog cartDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        shop = (ShopBean) getIntent().getSerializableExtra(Constant.EXTRA_SHOP);
        if (shop == null) {
            finish();
            return;
        }
        cartDao = new CartDao(this);
        initHeader();
        initMenu();
        initCartBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartBar();
    }

    private void initHeader() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(shop.getShopName());

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView ivCover = findViewById(R.id.iv_cover);
        Picasso.get()
                .load(shop.getLogo())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivCover);

        TextView tvName = findViewById(R.id.tv_shop_name);
        tvName.setText(shop.getShopName());

        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText(String.valueOf(shop.getScore()));

        TextView tvMeta = findViewById(R.id.tv_meta);
        tvMeta.setText(getString(R.string.shop_meta,
                shop.getMonthSale(),
                Constant.money(shop.getMinPrice()),
                Constant.money(shop.getDeliveryFee())));

        TextView tvNotice = findViewById(R.id.tv_notice);
        tvNotice.setText(getString(R.string.shop_notice, shop.getAnnouncement()));
    }

    private void initMenu() {
        RecyclerView rvMenu = findViewById(R.id.rv_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter menuAdapter = new MenuAdapter(shop.getFoods(), new MenuAdapter.OnMenuClickListener() {
            @Override
            public void onAddClick(FoodBean food) {
                cartDao.add(shop.getShopId(), food);
                refreshCartBar();
                Toast.makeText(ShopDetailActivity.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(FoodBean food) {
                Intent intent = new Intent(ShopDetailActivity.this, FoodActivity.class);
                intent.putExtra(Constant.EXTRA_FOOD, food);
                intent.putExtra(Constant.EXTRA_SHOP_ID, shop.getShopId());
                startActivity(intent);
            }
        });
        rvMenu.setAdapter(menuAdapter);
    }

    private void initCartBar() {
        tvBadge = findViewById(R.id.tv_badge);
        tvTotal = findViewById(R.id.tv_total);
        tvFee = findViewById(R.id.tv_fee);

        tvFee.setText(getString(R.string.cart_fee, Constant.money(shop.getDeliveryFee())));

        findViewById(R.id.fl_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartDao.countByShop(shop.getShopId()) > 0) {
                    showCartDialog();
                } else {
                    Toast.makeText(ShopDetailActivity.this, R.string.cart_is_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_settle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrder();
            }
        });
    }

    /** 把该店购物车整份传给订单页 */
    private void goToOrder() {
        List<CartBean> list = cartDao.listByShop(shop.getShopId());
        if (list.isEmpty()) {
            Toast.makeText(this, R.string.cart_is_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(Constant.EXTRA_CART_LIST, new ArrayList<>(list));
        intent.putExtra(Constant.EXTRA_SHOP_NAME, shop.getShopName());
        intent.putExtra(Constant.EXTRA_SHOP_ID, shop.getShopId());
        intent.putExtra(Constant.EXTRA_DELIVERY_FEE, shop.getDeliveryFee());
        startActivity(intent);
    }

    private void refreshCartBar() {
        int count = cartDao.countByShop(shop.getShopId());
        double total = cartDao.totalPriceByShop(shop.getShopId());

        if (count > 0) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvBadge.setVisibility(View.GONE);
        }
        tvTotal.setText(getString(R.string.money, Constant.money(total)));
        findViewById(R.id.btn_settle).setAlpha(count > 0 ? 1f : 0.45f);
    }

    private void showCartDialog() {
        if (cartDialog != null && cartDialog.isShowing()) {
            return;
        }
        cartDialog = new Dialog(this, R.style.Theme_FruitShop_BottomSheet);
        cartDialog.setContentView(R.layout.dialog_car);

        Window window = cartDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }

        RecyclerView rvCar = cartDialog.findViewById(R.id.rv_car);
        rvCar.setLayoutManager(new LinearLayoutManager(this));
        carAdapter = new CarAdapter(new CarAdapter.OnCarActionListener() {
            @Override
            public void onPlus(CartBean item) {
                cartDao.add(item.getShopId(), toFood(item));
                reloadCartList();
            }

            @Override
            public void onMinus(CartBean item) {
                cartDao.minus(item.getShopId(), item.getFoodId());
                reloadCartList();
            }

            @Override
            public void onDelete(CartBean item) {
                cartDao.delete(item.getShopId(), item.getFoodId());
                reloadCartList();
            }
        });
        rvCar.setAdapter(carAdapter);
        carAdapter.setData(cartDao.listByShop(shop.getShopId()));

        cartDialog.findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearDialog();
            }
        });

        cartDialog.setOnDismissListener(dialog -> refreshCartBar());
        cartDialog.show();
    }

    private void reloadCartList() {
        List<CartBean> list = cartDao.listByShop(shop.getShopId());
        if (list.isEmpty()) {
            if (cartDialog != null) {
                cartDialog.dismiss();
            }
            refreshCartBar();
            return;
        }
        carAdapter.setData(list);
        refreshCartBar();
    }

    private void showClearDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_FruitShop_Alert);
        dialog.setContentView(R.layout.dialog_clear);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDao.clearByShop(shop.getShopId());
                dialog.dismiss();
                if (cartDialog != null) {
                    cartDialog.dismiss();
                }
                refreshCartBar();
            }
        });
        dialog.show();
    }

    private FoodBean toFood(CartBean item) {
        FoodBean food = new FoodBean();
        food.setFoodId(item.getFoodId());
        food.setFoodName(item.getFoodName());
        food.setPrice(item.getPrice());
        food.setImage(item.getImage());
        return food;
    }
}
