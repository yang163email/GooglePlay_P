package com.yan.googleplay.fragment;

import android.graphics.Color;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.util.UiUtil;

import java.util.Random;

/**
 * Created by 楠GG on 2017/5/25.
 */

public class OtherFragment extends BaseFragment {
    @Override
    protected View onPostExcute() {
        TextView textView = new TextView(UiUtil.getContext());
        textView.setText("从服务器拿到的otherview");
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        SystemClock.sleep(2000);
        HeimaAsyncTask.Result[] results = {HeimaAsyncTask.Result.ERROR, HeimaAsyncTask.Result.EMPTY, HeimaAsyncTask.Result.SUCCESS};
        Random random = new Random();
        return results[random.nextInt(3)];
    }
}
