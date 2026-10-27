package cn.xrj.fruitshop.adapter;

import android.content.Context;
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
import cn.xrj.fruitshop.bean.ShopBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 首页店铺列表适配器。
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopHolder> {

    public interface OnShopClickListener {
        void onShopClick(ShopBean shop);
    }

    private final List<ShopBean> shops;
    private final OnShopClickListener listener;

    public ShopAdapter(List<ShopBean> shops, OnShopClickListener listener) {
        this.shops = shops;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ShopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopHolder holder, int position) {
        holder.bind(shops.get(position));
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    class ShopHolder extends RecyclerView.ViewHolder {

        private final ImageView ivCover;
        private final TextView tvName;
        private final TextView tvScore;
        private final TextView tvMeta;
        private final TextView tvNotice;
        private final TextView tvDeliver;

        ShopHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvName = itemView.findViewById(R.id.tv_shop_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvMeta = itemView.findViewById(R.id.tv_meta);
            tvNotice = itemView.findViewById(R.id.tv_notice);
            tvDeliver = itemView.findViewById(R.id.tv_deliver);
        }

        void bind(final ShopBean shop) {
            Context context = itemView.getContext();
            tvName.setText(shop.getShopName());
            tvScore.setText(String.valueOf(shop.getScore()));
            tvMeta.setText(context.getString(R.string.shop_meta,
                    shop.getMonthSale(),
                    Constant.money(shop.getMinPrice()),
                    Constant.money(shop.getDeliveryFee())));
            tvNotice.setText(context.getString(R.string.shop_notice, shop.getAnnouncement()));
            tvDeliver.setText(context.getString(R.string.shop_deliver,
                    shop.getDeliverTime(), shop.getDistance()));

            Picasso.get()
                    .load(shop.getLogo())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(ivCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onShopClick(shop);
                    }
                }
            });
        }
    }
}
