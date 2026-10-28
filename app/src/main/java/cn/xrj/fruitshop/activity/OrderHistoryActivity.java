package cn.xrj.fruitshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.adapter.OrderHistoryAdapter;
import cn.xrj.fruitshop.bean.OrderBean;
import cn.xrj.fruitshop.db.OrderDao;

/**
 * 我的订单，查 orders 表，每条订单再把 order_item 的明细补上。
 */
public class OrderHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.title_order_history);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        OrderDao orderDao = new OrderDao(this);
        List<OrderBean> orders = orderDao.listAll();
        for (OrderBean order : orders) {
            order.setItems(orderDao.listItems(order.getId()));
        }

        RecyclerView rvOrder = findViewById(R.id.rv_order);
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setAdapter(new OrderHistoryAdapter(orders));

        findViewById(R.id.ll_empty).setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
