package cn.xrj.fruitshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.bean.ShopBean;
import cn.xrj.fruitshop.utils.Constant;

/**
 * 店铺详情页。头部信息已经接上，菜单列表和购物车栏在后续步骤补。
 */
public class ShopDetailActivity extends AppCompatActivity {

    private ShopBean shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        shop = (ShopBean) getIntent().getSerializableExtra(Constant.EXTRA_SHOP);
        if (shop == null) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(shop.getShopName());

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView ivCover = findViewById(R.id.iv_cover);
        Picasso.get()
                .load(shop.getLogo())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivCover);

        TextView tvName = findViewById(R.id.tv_shop_name);
        tvName.setText(shop.getShopName());

        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText(String.valueOf(shop.getScore()));

        TextView tvMeta = findViewById(R.id.tv_meta);
        tvMeta.setText(getString(R.string.shop_meta,
                shop.getMonthSale(),
                Constant.money(shop.getMinPrice()),
                Constant.money(shop.getDeliveryFee())));

        TextView tvNotice = findViewById(R.id.tv_notice);
        tvNotice.setText(getString(R.string.shop_notice, shop.getAnnouncement()));
    }
}
