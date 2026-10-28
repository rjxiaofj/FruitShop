package cn.xrj.fruitshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.OrderBean;
import cn.xrj.fruitshop.bean.OrderItemBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 历史订单列表适配器，一行一张订单卡片。
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHolder> {

    private final List<OrderBean> orders;

    public OrderHistoryAdapter(List<OrderBean> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        OrderBean order = orders.get(position);
        holder.tvShopName.setText(order.getShopName());
        holder.tvStatus.setText(statusText(holder, order.getStatus()));
        holder.tvOrderNo.setText(holder.itemView.getContext()
                .getString(R.string.order_no, order.getOrderNo()));

        StringBuilder goods = new StringBuilder();
        int count = 0;
        if (order.getItems() != null) {
            for (OrderItemBean item : order.getItems()) {
                if (goods.length() > 0) {
                    goods.append("  ");
                }
                goods.append(item.getFoodName()).append(" x").append(item.getCount());
                count += item.getCount();
            }
        }
        holder.tvGoods.setText(goods.toString());
        holder.tvCount.setText(holder.itemView.getContext().getString(R.string.order_count, count));
        holder.tvTotal.setText(holder.itemView.getContext()
                .getString(R.string.money, Constant.money(order.getTotalPrice())));
    }

    private String statusText(OrderHolder holder, int status) {
        int res;
        if (status == OrderBean.STATUS_PAID) {
            res = R.string.order_status_paid;
        } else if (status == OrderBean.STATUS_DONE) {
            res = R.string.order_status_done;
        } else {
            res = R.string.order_status_unpaid;
        }
        return holder.itemView.getContext().getString(res);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder {

        final TextView tvShopName;
        final TextView tvStatus;
        final TextView tvOrderNo;
        final TextView tvGoods;
        final TextView tvCount;
        final TextView tvTotal;

        OrderHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvGoods = itemView.findViewById(R.id.tv_goods);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvTotal = itemView.findViewById(R.id.tv_total);
        }
    }
}
