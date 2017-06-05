package com.yan.googleplay.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yan.googleplay.util.UiUtil;

/**
 * Created by 楠GG on 2017/5/25.
 */

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    private HeimaAsyncTask mCommonView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
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
        return mCommonView.getCurrentView();
    }

    protected void onPreExecute() {
    }

    protected abstract View onPostExcute();

    public abstract HeimaAsyncTask.Result doInBackground();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);

//        mCommonView.getDataFromServer();
    }

    public void loadData() {
        mCommonView.getDataFromServer();
    }
}
