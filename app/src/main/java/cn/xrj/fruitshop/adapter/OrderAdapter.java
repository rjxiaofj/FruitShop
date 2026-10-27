package cn.xrj.fruitshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 订单页的商品清单适配器。
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private final List<CartBean> items;

    public OrderAdapter(List<CartBean> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder {

        private final ImageView ivFood;
        private final TextView tvName;
        private final TextView tvCount;
        private final TextView tvSubtotal;

        OrderHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
        }

        void bind(CartBean item) {
            tvName.setText(item.getFoodName());
            tvCount.setText(itemView.getContext().getString(R.string.count_format, item.getCount()));
            tvSubtotal.setText(itemView.getContext().getString(R.string.money,
                    Constant.money(item.getSubtotal())));

            Picasso.get()
                    .load(item.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(ivFood);
        }
    }
}
