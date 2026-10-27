package cn.xrj.fruitshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.FoodBean;
import cn.xrj.fruitshop.db.CartDao;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 商品详情，对话框样式的 Activity，浮在店铺详情页上面。
 */
public class FoodActivity extends AppCompatActivity {

    private FoodBean food;
    private int shopId;
    private CartDao cartDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        food = (FoodBean) getIntent().getSerializableExtra(Constant.EXTRA_FOOD);
        shopId = getIntent().getIntExtra(Constant.EXTRA_SHOP_ID, 0);
        if (food == null) {
            finish();
            return;
        }
        cartDao = new CartDao(this);
        initView();
    }

    private void initView() {
        ImageView ivFood = findViewById(R.id.iv_food);
        Picasso.get()
                .load(food.getImage())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivFood);

        TextView tvName = findViewById(R.id.tv_food_name);
        tvName.setText(food.getFoodName());

        TextView tvIngredients = findViewById(R.id.tv_ingredients);
        tvIngredients.setText(food.getIngredients() + " · " + food.getTaste());

        TextView tvPrice = findViewById(R.id.tv_price);
        tvPrice.setText(getString(R.string.money, Constant.money(food.getPrice())));

        TextView tvDesc = findViewById(R.id.tv_desc);
        tvDesc.setText(food.getDescription());

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDao.add(shopId, food);
                Toast.makeText(FoodActivity.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
