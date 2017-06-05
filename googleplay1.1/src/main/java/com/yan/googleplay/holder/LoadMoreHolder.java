package com.yan.googleplay.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yan.googleplay.R;
import com.yan.googleplay.base.BasicAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 楠GG on 2017/5/28.
 */

public class LoadMoreHolder extends BasicHolder {
    @BindView(R.id.item_loadmore_container_loading)
    LinearLayout mItemLoadmoreContainerLoading;
    @BindView(R.id.item_loadmore_tv_retry)
    TextView mItemLoadmoreTvRetry;
    @BindView(R.id.item_loadmore_container_retry)
    LinearLayout mItemLoadmoreContainerRetry;

    public static final int STATE_NONE = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_RETRY = 2;
    private BasicAdapter mBasicAdapter;

    public LoadMoreHolder(final View view) {
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasicAdapter.loadMore(view);
            }
        });
    }

    public void setData(int state) {
        switch (state) {
            case STATE_NONE:
                mItemLoadmoreContainerLoading.setVisibility(View.GONE);
                mItemLoadmoreContainerRetry.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                mItemLoadmoreContainerLoading.setVisibility(View.VISIBLE);
                mItemLoadmoreContainerRetry.setVisibility(View.GONE);
                break;
            case STATE_RETRY:
                mItemLoadmoreContainerLoading.setVisibility(View.GONE);
                mItemLoadmoreContainerRetry.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setAdapter(BasicAdapter tBasicAdapter) {
        this.mBasicAdapter = tBasicAdapter;
    }
}
