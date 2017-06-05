package com.yan.googleplay.fragment;

import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yan.googleplay.R;
import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.BasicAdapter;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.bean.SubjectBean;
import com.yan.googleplay.holder.BasicHolder;
import com.yan.googleplay.protocol.SubjectProtocol;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class SubjectFragment extends BaseFragment {
    List<SubjectBean> mDatas = new ArrayList<>();

    @Override
    protected View onPostExcute() {
        ListView listView = new ListView(UiUtil.getContext());
        listView.setAdapter(new SubjectAdapter(mDatas, R.layout.item_subject));
        return listView;
    }

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        SystemClock.sleep(2000);
        SubjectProtocol subjectProtocol = new SubjectProtocol();
        Map<String, String> map = new HashMap<>();
        map.put("index", "0");
        subjectProtocol.setParams(map);
        try {
            mDatas = subjectProtocol.loadData();
            if (mDatas == null || mDatas.size() == 0) {
                return HeimaAsyncTask.Result.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.SUCCESS;
    }

    private class SubjectAdapter extends BasicAdapter {

        public SubjectAdapter(List data, int layoutResId) {
            super(data, layoutResId);
        }

        @Override
        protected List getMoreDataFromServer() throws Exception {
            return null;
        }

        @Override
        protected BasicHolder createBasicHolder(View convertView, int position) {
            return new SubjectHolder(convertView);
        }

        @Override
        protected void onBindViewHolder(BasicHolder holder, int position) {
            SubjectHolder subjectHolder = (SubjectHolder) holder;
            subjectHolder.setData(mDatas.get(position));
        }
    }

    static class SubjectHolder extends BasicHolder {
        @BindView(R.id.item_subject_iv_icon)
        ImageView mItemSubjectIvIcon;
        @BindView(R.id.item_subject_tv_title)
        TextView mItemSubjectTvTitle;

        SubjectHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(SubjectBean subjectBean) {
            mItemSubjectTvTitle.setText(subjectBean.getDes());
            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + subjectBean.getUrl()).into(mItemSubjectIvIcon);
        }
    }
}
