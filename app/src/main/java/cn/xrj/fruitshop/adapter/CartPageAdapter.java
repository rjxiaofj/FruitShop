package cn.xrj.fruitshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 购物车页适配器，按店铺分组展示，店铺标题行和商品行用两种视图。
 */
public class CartPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SHOP = 0;
    private static final int TYPE_FOOD = 1;

    public interface OnCartPageListener {
        void onPlus(CartBean item);

        void onMinus(CartBean item);

        void onDelete(CartBean item);

        void onSettle(ShopSection section);
    }

    /** 一家店的购物车分组 */
    public static class ShopSection {
        public final int shopId;
        public final String shopName;
        public final double deliveryFee;
        public final List<CartBean> items = new ArrayList<>();

        public ShopSection(int shopId, String shopName, double deliveryFee) {
            this.shopId = shopId;
            this.shopName = shopName;
            this.deliveryFee = deliveryFee;
        }

        public int getCount() {
            int total = 0;
            for (CartBean item : items) {
                total += item.getCount();
            }
            return total;
        }

        public double getSubtotal() {
            double total = 0;
            for (CartBean item : items) {
                total += item.getSubtotal();
            }
            return total;
        }
    }

    private final List<Object> rows = new ArrayList<>();
    private final OnCartPageListener listener;

    public CartPageAdapter(OnCartPageListener listener) {
        this.listener = listener;
    }

    public void setRows(List<Object> data) {
        rows.clear();
        if (data != null) {
            rows.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof ShopSection ? TYPE_SHOP : TYPE_FOOD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SHOP) {
            return new ShopHolder(inflater.inflate(R.layout.item_cart_shop, parent, false));
        }
        return new FoodHolder(inflater.inflate(R.layout.item_cart_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);
        if (holder instanceof ShopHolder) {
            ((ShopHolder) holder).bind((ShopSection) row);
        } else {
            boolean last = position == rows.size() - 1
                    || rows.get(position + 1) instanceof ShopSection;
            ((FoodHolder) holder).bind((CartBean) row, last);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    class ShopHolder extends RecyclerView.ViewHolder {

        private final TextView tvShopName;
        private final TextView tvMeta;
        private final TextView btnSettle;

        ShopHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            tvMeta = itemView.findViewById(R.id.tv_shop_meta);
            btnSettle = itemView.findViewById(R.id.btn_settle_shop);
        }

        void bind(final ShopSection section) {
            tvShopName.setText(section.shopName);
            tvMeta.setText(itemView.getContext().getString(R.string.cart_shop_meta,
                    section.getCount(), Constant.money(section.getSubtotal())));
            btnSettle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSettle(section);
                    }
                }
            });
        }
    }

    class FoodHolder extends RecyclerView.ViewHolder {

        private final ImageView ivFood;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvCount;
        private final ImageView btnMinus;
        private final ImageView btnPlus;
        private final ImageView btnDelete;

        FoodHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        void bind(final CartBean item, boolean last) {
            itemView.setBackgroundResource(last ? R.drawable.bg_cart_bottom : R.color.bgCard);

            tvName.setText(item.getFoodName());
            tvPrice.setText(itemView.getContext().getString(R.string.money,
                    Constant.money(item.getPrice())));
            tvCount.setText(String.valueOf(item.getCount()));

            Picasso.get()
                    .load(item.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(ivFood);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPlus(item);
                    }
                }
            });
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onMinus(item);
                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDelete(item);
                    }
                }
            });
        }
    }
}
