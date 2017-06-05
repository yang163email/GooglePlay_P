package com.yan.googleplay.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.yan.googleplay.util.UiUtil;

/**
 * Created by 楠GG on 2017/6/2.
 */

public abstract class BaseActivity extends Activity {
    private HeimaAsyncTask mCommonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCommonView == null) {
            mCommonView = new HeimaAsyncTask(UiUtil.getContext()) {
                @Override
                protected void initView() {
                    super.initView();
                    onPreExecute();
                }

                @Override
                public Result getServerData() {
                    return doInBackground();
                }

                @Override
                protected View getSuccessView() {
                    return onPostExcute();
                }
            };
        }
        setContentView(mCommonView.getCurrentView());
    }

    protected void onPreExecute() {
    }

    protected abstract View onPostExcute();

    public abstract HeimaAsyncTask.Result doInBackground();

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    public void loadData() {
        mCommonView.getDataFromServer();
    }
}
