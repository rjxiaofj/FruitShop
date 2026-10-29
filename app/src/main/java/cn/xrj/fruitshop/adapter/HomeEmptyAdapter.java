package cn.xrj.fruitshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.xrj.fruitshop.R;

/**
 * 首页空数据适配器：店铺列表为空时显示在头部下面。
 */
public class HomeEmptyAdapter extends RecyclerView.Adapter<HomeEmptyAdapter.EmptyHolder> {

    private String text = "";

    public void setText(String text) {
        this.text = text == null ? "" : text;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    @NonNull
    @Override
    public EmptyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_empty, parent, false);
        return new EmptyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyHolder holder, int position) {
        holder.tvText.setText(text);
    }

    @Override
    public int getItemCount() {
        return text.isEmpty() ? 0 : 1;
    }

    class EmptyHolder extends RecyclerView.ViewHolder {

        final TextView tvText;

        EmptyHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_empty_text);
        }
    }
}
