package cn.xrj.fruitshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import cn.xrj.fruitshop.R;

/**
 * 首页轮播广告适配器。
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {

    private final String[] images;

    public BannerAdapter(String[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        Picasso.get()
                .load(images[position])
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.ivBanner);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class BannerHolder extends RecyclerView.ViewHolder {

        final ImageView ivBanner;

        BannerHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
        }
    }
}
