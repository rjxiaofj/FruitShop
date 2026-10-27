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
import cn.xrj.fruitshop.bean.FoodBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 店铺详情页的菜单列表适配器。
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {

    public interface OnMenuClickListener {
        void onAddClick(FoodBean food);

        void onItemClick(FoodBean food);
    }

    private final List<FoodBean> foods;
    private final OnMenuClickListener listener;

    public MenuAdapter(List<FoodBean> foods, OnMenuClickListener listener) {
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder holder, int position) {
        holder.bind(foods.get(position));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    class MenuHolder extends RecyclerView.ViewHolder {

        private final ImageView ivFood;
        private final TextView tvName;
        private final TextView tvIngredients;
        private final TextView tvTaste;
        private final TextView tvPrice;
        private final ImageView ivAdd;

        MenuHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvIngredients = itemView.findViewById(R.id.tv_ingredients);
            tvTaste = itemView.findViewById(R.id.tv_taste);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivAdd = itemView.findViewById(R.id.iv_add);
        }

        void bind(final FoodBean food) {
            tvName.setText(food.getFoodName());
            tvIngredients.setText(food.getIngredients());
            tvTaste.setText(food.getTaste());
            tvPrice.setText(itemView.getContext().getString(R.string.money,
                    Constant.money(food.getPrice())));

            Picasso.get()
                    .load(food.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .into(ivFood);

            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAddClick(food);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(food);
                    }
                }
            });
        }
    }
}
