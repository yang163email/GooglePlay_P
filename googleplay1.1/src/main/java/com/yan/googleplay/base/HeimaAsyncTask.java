package com.yan.googleplay.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.yan.googleplay.R;
import com.yan.googleplay.manager.ThreadManager;
import com.yan.googleplay.util.UiUtil;

import static com.yan.googleplay.util.UiUtil.getContext;

/**
 * Created by 楠GG on 2017/5/25.
 */

public abstract class HeimaAsyncTask {
    private FrameLayout mCurrentView;

    public FrameLayout getCurrentView() {
        return mCurrentView;
    }

    /**
     * 当前状态
     */
    private int mCurrentState = STATE_NONE;
    /**
     * 请求前状态
     */
    public static final int STATE_NONE = 0;
    /**
     * 加载中
     */
    public static final int STATE_LOADING = 1;

    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_SUCCESS = 4;

    /**是否正在加载中*/
    private boolean isLoading;
    private FrameLayout mFragment;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;
    private View mSuccessView;


    public HeimaAsyncTask(Context context) {
        mCurrentView = new FrameLayout(context);
        initView();
    }

    protected void initView() {
        // mFragment = new FrameLayout(getContext());

        //初始化
//        TextView textView = new TextView(UiUtil.getContext());
//        textView.setText("初始化的view");
//        textView.setTextColor(Color.BLACK);
//        textView.setGravity(Gravity.CENTER);
//        addView(textView);

        //loading view
        mLoadingView = View.inflate(getContext(), R.layout.pager_loading, null);
        mLoadingView.setVisibility(View.GONE);
        mCurrentView.addView(mLoadingView);

        //error view
        mErrorView = View.inflate(getContext(), R.layout.pager_error, null);
        mErrorView.setVisibility(View.GONE);
        mCurrentView.addView(mErrorView);

        //empty view
        mEmptyView = View.inflate(getContext(), R.layout.pager_empty, null);
        mEmptyView.setVisibility(View.GONE);
        mCurrentView.addView(mEmptyView);

        //success view

    }

    public void getDataFromServer() {
        //如果已经成功了，就不再请求服务器
        if(mCurrentState == STATE_SUCCESS) {
            return;
        }
        //判断是否在加载中
        if(isLoading) {
            return;
        }
        isLoading = true;
        mCurrentState = STATE_LOADING;
//        mLoadingView.setVisibility(View.VISIBLE);
        refreshUi();
//        new Thread(new loadDataTask()).start();
        ThreadManager.getNormalPool().execute(new loadDataTask());
    }

    private class loadDataTask implements Runnable {

        @Override
        public void run() {
            Result result = getServerData();
            mCurrentState = result.getState();
            safeRefreshUi();
        }
    }

    /**获取网络数据状态的枚举*/
    public enum Result {
        ERROR(STATE_ERROR),EMPTY(STATE_EMPTY),SUCCESS(STATE_SUCCESS);

        private int state;

        Result(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    /**从服务器拿到响应状态*/
    public abstract Result getServerData();

    private void safeRefreshUi() {
        UiUtil.postTask(new Runnable() {
            @Override
            public void run() {
                refreshUi();
                isLoading = false;
            }
        });
    }

    private void refreshUi() {
        if(mSuccessView == null && mCurrentState == STATE_SUCCESS) {
            mSuccessView = getSuccessView();
            mCurrentView.addView(mSuccessView);
        }
        mLoadingView.setVisibility(mCurrentState == STATE_LOADING ? View.VISIBLE : View.GONE);
        mErrorView.setVisibility(mCurrentState == STATE_ERROR ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(mCurrentState == STATE_EMPTY ? View.VISIBLE : View.GONE);
        if(mSuccessView != null) {
            mSuccessView.setVisibility(mCurrentState == STATE_SUCCESS ? View.VISIBLE : View.GONE);
        }
    }

    protected abstract View getSuccessView();
}
