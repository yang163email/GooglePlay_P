package com.yan.googleplay.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.heima30.flowlayoutlib.FlowLayout;
import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.protocol.HotProtocol;
import com.yan.googleplay.util.UiUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by 楠GG on 2017/6/1.
 */

public class HotFragment extends BaseFragment {
    private Random mRandom = new Random();
    private List<String> mDatas;

    @Override
    protected View onPostExcute() {
        FlowLayout flowLayout = new FlowLayout(UiUtil.getContext());
        flowLayout.setPadding(8, 8, 8, 8);

        for(int i=0;i<mDatas.size();i++){
            TextView view = new TextView(UiUtil.getContext());
            view.setText(mDatas.get(i));

//            view.setBackgroundColor(Color.GRAY);
//            view.setBackgroundResource(R.drawable.hot_fragment_shape);
            //代码设置背景样式
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(16);
            int a = 200 + mRandom.nextInt(55);
            int r = 100 + mRandom.nextInt(155);
            int g = 100 + mRandom.nextInt(155);
            int b = 100 + mRandom.nextInt(155);
            gradientDrawable.setColor(Color.argb(a, r, g, b));
//            view.setBackgroundDrawable(gradientDrawable);

            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setCornerRadius(16);
            gradientDrawable2.setColor(Color.GRAY);

            //代码实现选择器
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable2);
            stateListDrawable.addState(new int[]{}, gradientDrawable);
            view.setBackgroundDrawable(stateListDrawable);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UiUtil.getContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
                }
            });

            view.setTextColor(Color.WHITE);
            view.setPadding(5, 5, 5, 5);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(20);

            flowLayout.addView(view);//触发重绘
        }
        return flowLayout;
    }

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        HotProtocol hotProtocol = new HotProtocol();
        try {
            mDatas = hotProtocol.loadData();
            if(mDatas == null || mDatas.size() == 0) {
                return HeimaAsyncTask.Result.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.SUCCESS;
    }
}
