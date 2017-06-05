package com.yan.googleplay.fragment;

import android.content.Intent;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yan.googleplay.R;
import com.yan.googleplay.activity.DetailActivity;
import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.BasicAdapter;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.bean.HomeBean;
import com.yan.googleplay.holder.BasicHolder;
import com.yan.googleplay.holder.HomePicHolder;
import com.yan.googleplay.manager.DownloadManager;
import com.yan.googleplay.protocol.HomeProtocol;
import com.yan.googleplay.util.CommonUtils;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;
import com.yan.googleplay.view.CircleProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 楠GG on 2017/5/25.
 */

public class HomeFragment extends BaseFragment {

    private List<HomeBean.AppItem> mDatas = new ArrayList<>();
    private List<String> mPics;

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        //SystemClock.sleep(2000);

        HomeProtocol homeProtocol = new HomeProtocol();
        try {
            HomeBean homeBean = homeProtocol.loadData("0");
            if (homeBean != null) {
                mDatas = homeBean.getList();
                mPics = homeBean.getPicture();
                if (mDatas != null && mDatas.size() != 0) {
                    return HeimaAsyncTask.Result.SUCCESS;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.EMPTY;

    }

    @Override
    protected View onPostExcute() {
        ListView listView = new ListView(UiUtil.getContext());
        listView.setAdapter(new HomeAdapter(mDatas, R.layout.item_app_info));

        //轮播图
        View picView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_home_picture, null);
        HomePicHolder homePicHolder = new HomePicHolder(picView);
        homePicHolder.setData(mPics);

        listView.addHeaderView(picView);
        return listView;
    }

    private class HomeAdapter extends BasicAdapter<HomeBean.AppItem> {

        public HomeAdapter(List<HomeBean.AppItem> data, int layoutResId) {
            super(data, layoutResId);
        }

        @Override
        protected boolean SupportLoadMore() {
            return true;
        }

        @Override
        protected BasicHolder createBasicHolder(View convertView, int position) {
            return new HomeHolder(convertView);
        }

        @Override
        protected void onBindViewHolder(BasicHolder holder, int position) {
            HomeHolder homeHolder = (HomeHolder) holder;
            HomeBean.AppItem appItem = mDatas.get(position);
            homeHolder.setData(appItem);
            DownloadManager.getInstance().addDownloadListener(appItem.getPackageName(), homeHolder);
        }

        @Override
        protected List<HomeBean.AppItem> getMoreDataFromServer() throws Exception {
            HomeProtocol homeProtocol = new HomeProtocol();
            HomeBean homeBean = homeProtocol.loadData(getCount() + "");
            if (homeBean != null) {
                return homeBean.getList();
            }
            return null;
        }
    }

    public static class HomeHolder extends BasicHolder implements DownloadManager.DownloadListener{
        @BindView(R.id.item_appinfo_iv_icon)
        ImageView mItemAppinfoIvIcon;
        @BindView(R.id.item_appinfo_tv_title)
        TextView mItemAppinfoTvTitle;
        @BindView(R.id.item_appinfo_rb_stars)
        RatingBar mItemAppinfoRbStars;
        @BindView(R.id.item_appinfo_tv_size)
        TextView mItemAppinfoTvSize;
        @BindView(R.id.item_appinfo_tv_des)
        TextView mItemAppinfoTvDes;

        @BindView(R.id.ll_download)
        LinearLayout mLLDownload;
        @BindView(R.id.iv_download)
        CircleProgressView mCircleProgressView;
        @BindView(R.id.tv_download)
        TextView mTvDownload;

        private HomeBean.AppItem mAppItem;
        private int mState;
        private long mProgress;

        public HomeHolder(View view) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UiUtil.getContext(), DetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("pkgname", mAppItem.getPackageName());
                    UiUtil.getContext().startActivity(intent);
                }
            });
        }

        public void setData(HomeBean.AppItem bean) {
            mAppItem = bean;
            //先设置成未下载的状态
            mTvDownload.setText("下载");
            //TODO:下载中状态


            mState = DownloadManager.getInstance().getState(mAppItem.getPackageName(), mAppItem.getSize());
            refreshUiByState(mState);


            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + bean.getIconUrl()).into(mItemAppinfoIvIcon);
            mItemAppinfoTvTitle.setText(bean.getName());
            mItemAppinfoRbStars.setRating(bean.getStars());
            mItemAppinfoTvSize.setText(Formatter.formatFileSize(UiUtil.getContext(), bean.getSize()));
            mItemAppinfoTvDes.setText(bean.getDes());
        }

        @OnClick(R.id.ll_download)
        public void download(View v) {
            switch (mState) {
                case DownloadManager.STATE_UNDOWNLOAD:
                    mCircleProgressView.setState(CircleProgressView.State.DOWNLOAD);
                    downloadApk();
                    break;
                case DownloadManager.STATE_WAITING:
                    cancel();
                    break;
                case DownloadManager.STATE_DOWNLOADING:
                    pause();
                    break;
                case DownloadManager.STATE_PAUSE:
                    downloadApk();
                    break;
                case DownloadManager.STATE_SUCCESS:
                    install();
                    break;
                case DownloadManager.STATE_ERROR:
                    downloadApk();
                    break;
                case DownloadManager.STATE_INSTALLED:
                    openApk();
                    break;
            }
        }

        private void openApk() {
            String pkgName = mAppItem.getPackageName();
            if(CommonUtils.isInstalled(UiUtil.getContext(), pkgName)){
                //如果已经安装，则打开
                CommonUtils.openApp(UiUtil.getContext(), pkgName);
            }
        }

        private void install() {
            String pkgName = mAppItem.getPackageName();
            //如果没有安装，则安装
            if(!CommonUtils.isInstalled(UiUtil.getContext(), pkgName)) {
                CommonUtils.installApp(UiUtil.getContext(),
                        DownloadManager.getInstance().getDownloadFile(pkgName));
            }
        }
        /**暂停*/
        private void pause() {
            DownloadManager.getInstance().pause(mAppItem.getPackageName());
        }

        /**取消*/
        private void cancel() {
            DownloadManager.getInstance().cancel(mAppItem.getPackageName());
            mState = DownloadManager.STATE_UNDOWNLOAD;
            refreshUiByState(mState);
        }

        private void downloadApk() {
            //下载应用
            DownloadManager.getInstance().download(mAppItem, this);
        }


        @Override
        public void onStateChange(String packageName, final int state) {
            //拦截不相关的应用
            if(!mAppItem.getPackageName().equals(packageName)) {
                return;
            }
            mState = state;
            UiUtil.postTask(new Runnable() {
                @Override
                public void run() {
                    refreshUiByState(state);
                }
            });
        }

        @Override
        public int setState() {
            return mState;
        }

        @Override
        public void onProgressChange(long progress) {
            mProgress = progress;
        }

        private void refreshUiByState(int state) {
            switch (state) {
                case DownloadManager.STATE_UNDOWNLOAD:
                    mTvDownload.setText("下载");
                    mCircleProgressView.setState(CircleProgressView.State.DOWNLOAD);
                    break;
                case DownloadManager.STATE_WAITING:
                    mTvDownload.setText("正在等待,点击取消");
                    mCircleProgressView.setState(CircleProgressView.State.WAITING);
                    break;
                case DownloadManager.STATE_DOWNLOADING:
                    int precent = (int) (mProgress * 100f / mAppItem.getSize());
                    mTvDownload.setText(precent + "%");
                    mCircleProgressView.setState(CircleProgressView.State.LOADING);
                    mCircleProgressView.setProgress(precent);
                    break;
                case DownloadManager.STATE_PAUSE:
                    mTvDownload.setText("继续下载");
                    mCircleProgressView.setState(CircleProgressView.State.PAUSE);
                    break;
                case DownloadManager.STATE_SUCCESS:
                    mTvDownload.setText("安装");
                    mCircleProgressView.setState(CircleProgressView.State.OPEN);
                    break;
                case DownloadManager.STATE_ERROR:
                    mTvDownload.setText("重试");
                    mCircleProgressView.setState(CircleProgressView.State.RETRY);
                    break;
                case DownloadManager.STATE_INSTALLED:
                    mTvDownload.setText("打开");
                    mCircleProgressView.setState(CircleProgressView.State.OPEN);
                    break;
            }
        }
    }
}
