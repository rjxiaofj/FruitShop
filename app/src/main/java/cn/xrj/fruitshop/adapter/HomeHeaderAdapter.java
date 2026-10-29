package cn.xrj.fruitshop.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import cn.xrj.fruitshop.R;

/**
 * 首页头部适配器：搜索栏和轮播作为列表第一项，跟着页面一起滚。
 */
public class HomeHeaderAdapter extends RecyclerView.Adapter<HomeHeaderAdapter.HeaderHolder> {

    public interface OnSearchListener {
        void onSearch(String keyword);
    }

    private static final long BANNER_INTERVAL = 3500L;

    private final String[] banners;
    private final OnSearchListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ViewPager2 vpBanner;
    private LinearLayout llDots;

    private final Runnable bannerTask = new Runnable() {
        @Override
        public void run() {
            if (vpBanner == null) {
                return;
            }
            int next = (vpBanner.getCurrentItem() + 1) % banners.length;
            vpBanner.setCurrentItem(next, true);
            mainHandler.postDelayed(this, BANNER_INTERVAL);
        }
    };

    public HomeHeaderAdapter(String[] banners, OnSearchListener listener) {
        this.banners = banners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderHolder holder, int position) {
        // 头部只有一项，控件在构造时已装配完成
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void startBanner() {
        mainHandler.postDelayed(bannerTask, BANNER_INTERVAL);
    }

    public void stopBanner() {
        mainHandler.removeCallbacks(bannerTask);
    }

    class HeaderHolder extends RecyclerView.ViewHolder {

        HeaderHolder(@NonNull View itemView) {
            super(itemView);
            vpBanner = itemView.findViewById(R.id.vp_banner);
            llDots = itemView.findViewById(R.id.ll_dots);
            EditText etSearch = itemView.findViewById(R.id.et_search);
            TextView btnSearch = itemView.findViewById(R.id.btn_search);

            vpBanner.setAdapter(new BannerAdapter(banners));
            buildDots(0);
            vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    buildDots(position);
                }
            });

            View.OnClickListener searchClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSearch(etSearch.getText().toString().trim());
                    }
                    hideKeyboard(itemView, etSearch);
                }
            };
            btnSearch.setOnClickListener(searchClick);

            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (listener != null) {
                            listener.onSearch(etSearch.getText().toString().trim());
                        }
                        hideKeyboard(itemView, etSearch);
                        return true;
                    }
                    return false;
                }
            });
        }

        private void buildDots(int selected) {
            llDots.removeAllViews();
            float density = itemView.getResources().getDisplayMetrics().density;
            int size = (int) (6 * density + 0.5f);
            int margin = (int) (3 * density + 0.5f);
            for (int i = 0; i < banners.length; i++) {
                View dot = new View(itemView.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                lp.setMargins(margin, 0, margin, 0);
                dot.setLayoutParams(lp);
                dot.setBackgroundResource(i == selected ? R.drawable.dot_selected : R.drawable.dot_normal);
                llDots.addView(dot);
            }
        }

        private void hideKeyboard(View view, EditText etSearch) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }
            etSearch.clearFocus();
        }
    }
}
