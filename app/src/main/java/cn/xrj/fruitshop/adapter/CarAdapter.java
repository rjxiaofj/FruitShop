package cn.xrj.fruitshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.CartBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 购物车明细适配器，加购、减购、删除都在这里回调出去。
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarHolder> {

    public interface OnCarActionListener {
        void onPlus(CartBean item);

        void onMinus(CartBean item);

        void onDelete(CartBean item);
    }

    private final List<CartBean> items = new ArrayList<>();
    private final OnCarActionListener listener;

    public CarAdapter(OnCarActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<CartBean> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CarHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvCount;
        private final ImageView btnMinus;
        private final ImageView btnPlus;
        private final ImageView btnDelete;

        CarHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_car_name);
            tvPrice = itemView.findViewById(R.id.tv_car_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        void bind(final CartBean item) {
            tvName.setText(item.getFoodName());
            tvPrice.setText(itemView.getContext().getString(R.string.money,
                    Constant.money(item.getPrice())));
            tvCount.setText(String.valueOf(item.getCount()));

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
