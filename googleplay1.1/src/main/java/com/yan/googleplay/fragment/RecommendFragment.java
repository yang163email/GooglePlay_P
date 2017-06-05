package com.yan.googleplay.fragment;

import android.graphics.Color;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.protocol.RecommendProtocol;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;
import com.yan.googleplay.view.StellarMap;

import java.util.List;
import java.util.Random;

/**
 * Created by 楠GG on 2017/6/1.
 */

public class RecommendFragment extends BaseFragment {
    private List<String> mDatas;
    private Random mRandom = new Random();

    @Override
    protected View onPostExcute() {
        StellarMap stellarMap = new StellarMap(UiUtil.getContext());
        stellarMap.setInnerPadding(8, 8, 8, 8); //内边距
        stellarMap.setRegularity(10, 10);   //网格raw,col个数，
        stellarMap.setAdapter(new RecommendAdapter());
        stellarMap.setGroup(0, true);   //设置起始位置及是否有动画效果

        return stellarMap;
    }

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        SystemClock.sleep(Constant.SLEEP_TIME);
        RecommendProtocol recommendProtocol = new RecommendProtocol();
        try {
            mDatas = recommendProtocol.loadData();
            if (mDatas == null || mDatas.size() == 0) {
                return HeimaAsyncTask.Result.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.SUCCESS;
    }

    private class RecommendAdapter implements StellarMap.Adapter {
        //每页显示的个数
        private static final int PAGE_SIZE = 12;

        //共有多少组
        @Override
        public int getGroupCount() {
            if (mDatas.size() % PAGE_SIZE == 0) {
                return mDatas.size() / PAGE_SIZE;
            }
            return mDatas.size() / PAGE_SIZE + 1;
        }

        //指定group显示的控件个数
        @Override
        public int getCount(int group) {
            if (group == getGroupCount() - 1) {
                if (mDatas.size() % PAGE_SIZE != 0) {
                    return mDatas.size() % PAGE_SIZE;
                }
            }
            return PAGE_SIZE;
        }

        @Override
        public View getView(int group, int position, View convertView) {
            int realPosition = group * PAGE_SIZE + position;
            TextView textView = new TextView(UiUtil.getContext());
            textView.setText(mDatas.get(realPosition));

            int a = 200 + mRandom.nextInt(55);
            int r = 100 + mRandom.nextInt(155);
            int g = 100 + mRandom.nextInt(155);
            int b = 100 + mRandom.nextInt(155);
            textView.setTextColor(Color.argb(a, r, g, b));

            float size = 15 + mRandom.nextInt(10);
            textView.setTextSize(size);
            return textView;
        }

        @Override
        public int getNextGroupOnPan(int group, float degree) {
            group --;
            if(group == -1) {
                group = getGroupCount() - 1;
            }
            return group;
        }

        //放大的下一组
        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            group ++;
            if(group == getGroupCount()) {
                group = 0;
            }
            return group;
        }
    }
}
