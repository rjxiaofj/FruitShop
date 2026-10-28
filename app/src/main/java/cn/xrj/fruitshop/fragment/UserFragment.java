package cn.xrj.fruitshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.xrj.fruitshop.R;
import cn.xrj.fruitshop.activity.LoginActivity;
import cn.xrj.fruitshop.activity.OrderHistoryActivity;
import cn.xrj.fruitshop.bean.UserBean;
import cn.xrj.fruitshop.utils.UserManager;

/**
 * 个人中心，展示登录用户信息，提供我的订单入口和退出登录。
 */
public class UserFragment extends Fragment {

    private TextView tvNickname;
    private TextView tvPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvPhone = view.findViewById(R.id.tv_phone);

        view.findViewById(R.id.ll_my_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OrderHistoryActivity.class));
            }
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getInstance(getContext()).logout();
                Toast.makeText(getContext(), R.string.tips_logout, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showUser();
    }

    private void showUser() {
        UserBean user = UserManager.getInstance(getContext()).getLoginUser();
        if (user == null) {
            tvNickname.setText(R.string.not_login);
            tvPhone.setText(R.string.login_hint);
            return;
        }
        tvNickname.setText(user.getNickname());
        tvPhone.setText(user.getPhone());
    }
}
