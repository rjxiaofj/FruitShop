package cn.xrj.fruitshop.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.adapter.OrderAdapter;
import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.bean.OrderBean;
import cn.xrj.fruitshop.bean.OrderItemBean;
import cn.xrj.fruitshop.db.CartDao;
import cn.xrj.fruitshop.db.OrderDao;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 订单页：收货信息、商品清单、金额合计、去支付。
 * 提交时写 orders 和 order_item 两张表，清空该店购物车，然后弹二维码模拟支付。
 */
public class OrderActivity extends AppCompatActivity {

    private final List<CartBean> items = new ArrayList<>();
    private String shopName;
    private int shopId;
    private double deliveryFee;
    private double subtotal;

    private EditText etAddress;
    private EditText etName;
    private EditText etPhone;

    private OrderDao orderDao;
    private CartDao cartDao;
    private int orderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        shopName = getIntent().getStringExtra(Constant.EXTRA_SHOP_NAME);
        shopId = getIntent().getIntExtra(Constant.EXTRA_SHOP_ID, 0);
        deliveryFee = getIntent().getDoubleExtra(Constant.EXTRA_DELIVERY_FEE, 0);
        ArrayList<CartBean> list =
                (ArrayList<CartBean>) getIntent().getSerializableExtra(Constant.EXTRA_CART_LIST);
        if (list == null || list.isEmpty()) {
            finish();
            return;
        }
        items.addAll(list);

        orderDao = new OrderDao(this);
        cartDao = new CartDao(this);

        initView();
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.title_order);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etAddress = findViewById(R.id.et_address);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);

        RecyclerView rvOrder = findViewById(R.id.rv_order);
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setAdapter(new OrderAdapter(items));

        for (CartBean item : items) {
            subtotal += item.getSubtotal();
        }
        double total = subtotal + deliveryFee;

        ((TextView) findViewById(R.id.tv_subtotal))
                .setText(getString(R.string.money, Constant.money(subtotal)));
        ((TextView) findViewById(R.id.tv_delivery))
                .setText(deliveryFee > 0
                        ? getString(R.string.money, Constant.money(deliveryFee))
                        : getString(R.string.free_delivery));
        ((TextView) findViewById(R.id.tv_total))
                .setText(getString(R.string.money, Constant.money(total)));
        ((TextView) findViewById(R.id.tv_total_hint))
                .setText(getString(R.string.order_total_hint, Constant.money(deliveryFee)));

        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();
            }
        });
    }

    private void submitOrder() {
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, R.string.tips_address, Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, R.string.tips_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        OrderBean order = new OrderBean();
        order.setOrderNo("FS" + System.currentTimeMillis());
        order.setShopName(shopName);
        order.setTotalPrice(subtotal + deliveryFee);
        order.setDeliveryFee(deliveryFee);
        order.setAddress(address);
        order.setPhone(phone);
        order.setStatus(OrderBean.STATUS_UNPAID);
        order.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date()));

        List<OrderItemBean> orderItems = new ArrayList<>();
        for (CartBean item : items) {
            orderItems.add(new OrderItemBean(item.getFoodName(), item.getPrice(), item.getCount()));
        }
        order.setItems(orderItems);

        orderId = (int) orderDao.insertOrder(order);
        cartDao.clearByShop(shopId);

        Toast.makeText(this, R.string.order_success, Toast.LENGTH_SHORT).show();
        showPayDialog(order.getTotalPrice());
    }

    private void showPayDialog(double total) {
        final Dialog dialog = new Dialog(this, R.style.Theme_FruitShop_Alert);
        dialog.setContentView(R.layout.dialog_qr);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvAmount = dialog.findViewById(R.id.tv_amount);
        tvAmount.setText(getString(R.string.qr_amount, Constant.money(total)));

        dialog.findViewById(R.id.btn_paid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDao.updateStatus(orderId, OrderBean.STATUS_PAID);
                dialog.dismiss();
                Toast.makeText(OrderActivity.this, R.string.pay_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }
}
