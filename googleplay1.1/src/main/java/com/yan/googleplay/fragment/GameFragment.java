package com.yan.googleplay.fragment;

import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;

import com.yan.googleplay.R;
import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.BasicAdapter;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.bean.HomeBean;
import com.yan.googleplay.holder.BasicHolder;
import com.yan.googleplay.protocol.GameProtocol;
import com.yan.googleplay.util.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class GameFragment extends BaseFragment {
    private List<HomeBean.AppItem> mDatas = new ArrayList<>();

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        SystemClock.sleep(2000);
        GameProtocol gameProtocol = new GameProtocol();
        Map<String, String> map = new HashMap<>();
        map.put("index", "0");
        gameProtocol.setParams(map);
        try {
            mDatas = gameProtocol.loadData();
            if(mDatas == null || mDatas.size() == 0) {
                return HeimaAsyncTask.Result.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.SUCCESS;
    }

    @Override
    protected View onPostExcute() {
        ListView listView = new ListView(UiUtil.getContext());
        listView.setAdapter(new GameAdapter(mDatas, R.layout.item_app_info));

        return listView;
    }

    private class GameAdapter extends BasicAdapter {

        @Override
        protected boolean SupportLoadMore() {
            return true;
        }

        public GameAdapter(List data, int layoutResId) {
            super(data, layoutResId);
        }

        @Override
        protected List getMoreDataFromServer() throws Exception {
            GameProtocol gameProtocol = new GameProtocol();
            Map<String, String> map = new HashMap<>();
            map.put("index", String.valueOf(getCount()));
            gameProtocol.setParams(map);
            return gameProtocol.loadData();
        }

        @Override
        protected BasicHolder createBasicHolder(View convertView, int position) {
            return new HomeFragment.HomeHolder(convertView);
        }

        @Override
        protected void onBindViewHolder(BasicHolder holder, int position) {
            HomeFragment.HomeHolder appHolder = (HomeFragment.HomeHolder) holder;
            appHolder.setData(mDatas.get(position));
        }
    }
}
