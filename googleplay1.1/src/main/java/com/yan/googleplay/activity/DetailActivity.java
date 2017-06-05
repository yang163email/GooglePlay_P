package com.yan.googleplay.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yan.googleplay.R;
import com.yan.googleplay.base.BaseActivity;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.bean.DetailBean;
import com.yan.googleplay.manager.DownloadManager;
import com.yan.googleplay.protocol.DetailProtocol;
import com.yan.googleplay.util.CommonUtils;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;
import com.yan.googleplay.view.RatioLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 楠GG on 2017/6/2.
 */

public class DetailActivity extends BaseActivity {
    private static final String TAG = "DetailActivity";

    @BindView(R.id.detail_toolbar)
    Toolbar mDetailToolbar;
    @BindView(R.id.detail_download_container)
    FrameLayout mDetailDownloadContainer;
    @BindView(R.id.detail_info_container)
    FrameLayout mDetailInfoContainer;
    @BindView(R.id.detail_safe_container)
    FrameLayout mDetailSafeContainer;
    @BindView(R.id.detail_pic_container)
    FrameLayout mDetailPicContainer;
    @BindView(R.id.detail_des_container)
    FrameLayout mDetailDesContainer;
    private DetailBean mDetailBean;
    private String mPkgname;
    private BottomHolder mBottomHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPkgname = getIntent().getStringExtra("pkgname");
        }
    }

    @Override
    protected View onPostExcute() {
        View detailView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.activity_app_detail, null);
        ButterKnife.bind(this, detailView);
        mDetailToolbar.setNavigationIcon(R.mipmap.ic_menu_back);
        mDetailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //info view
        View infoView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_app_detail_info, null);
        InfoHolder infoHolder = new InfoHolder(infoView);
        infoHolder.setData(mDetailBean);
        mDetailInfoContainer.addView(infoView);

        //safe view
        View safeView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_app_detail_safe, null);
        SafeHolder safeHolder = new SafeHolder(safeView);
        safeHolder.setData(mDetailBean);
        mDetailSafeContainer.addView(safeView);

        //pic view
        View picView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_app_detail_pic, null);
        PicHolder picHolder = new PicHolder(picView);
        picHolder.setData(mDetailBean);
        mDetailPicContainer.addView(picView);

        //des view
        View desView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_app_detail_des, null);
        DesHolder desHolder = new DesHolder(desView);
        desHolder.setData(mDetailBean);
        mDetailDesContainer.addView(desView);

        //bottom view
        View bottomView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_app_detail_bottom, null);
        mBottomHolder = new BottomHolder(bottomView);
        mBottomHolder.setData(mDetailBean);
        DownloadManager.getInstance().addDownloadListener(mDetailBean.packageName, mBottomHolder);
        mDetailDownloadContainer.addView(bottomView);

        return detailView;
    }

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        SystemClock.sleep(2000);
        DetailProtocol detailProtocol = new DetailProtocol();
        Map<String, String> map = new HashMap<>();
        map.put("packageName", mPkgname);
        detailProtocol.setParams(map);
        try {
            mDetailBean = detailProtocol.loadData();
            if (mDetailBean == null) {
                return HeimaAsyncTask.Result.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.SUCCESS;
    }

    class InfoHolder {
        @BindView(R.id.app_detail_info_iv_icon)
        ImageView mAppDetailInfoIvIcon;
        @BindView(R.id.app_detail_info_tv_name)
        TextView mAppDetailInfoTvName;
        @BindView(R.id.app_detail_info_rb_star)
        RatingBar mAppDetailInfoRbStar;
        @BindView(R.id.app_detail_info_tv_downloadnum)
        TextView mAppDetailInfoTvDownloadnum;
        @BindView(R.id.app_detail_info_tv_version)
        TextView mAppDetailInfoTvVersion;
        @BindView(R.id.app_detail_info_tv_time)
        TextView mAppDetailInfoTvTime;
        @BindView(R.id.app_detail_info_tv_size)
        TextView mAppDetailInfoTvSize;

        public InfoHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(DetailBean detailBean) {
            mAppDetailInfoTvName.setText(detailBean.name);
            mAppDetailInfoRbStar.setRating((float) detailBean.stars);
            mAppDetailInfoTvDownloadnum.setText(UiUtil.getContext().getString(R.string.detail_download_num, detailBean.downloadNum));
            mAppDetailInfoTvVersion.setText("版本：" + detailBean.version);
            mAppDetailInfoTvTime.setText("时间：" + detailBean.date);
            mAppDetailInfoTvSize.setText("大小：" + Formatter.formatFileSize(UiUtil.getContext(), detailBean.size));
            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + detailBean.iconUrl)
                    .placeholder(R.mipmap.ic_default).error(R.mipmap.ic_default).into(mAppDetailInfoIvIcon);
        }
    }

    class SafeHolder {
        @BindView(R.id.app_detail_safe_iv_arrow)
        ImageView mAppDetailSafeIvArrow;
        @BindView(R.id.app_detail_safe_pic_container)
        LinearLayout mAppDetailSafePicContainer;
        @BindView(R.id.app_detail_safe_des_container)
        LinearLayout mAppDetailSafeDesContainer;

        private boolean isOpen;

        public SafeHolder(View view) {
            ButterKnife.bind(this, view);

            //设置初始状态
            ViewGroup.LayoutParams layoutParams = mAppDetailSafeDesContainer.getLayoutParams();
            layoutParams.height = 0;
            mAppDetailSafeDesContainer.setLayoutParams(layoutParams);
            mAppDetailSafeIvArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            });
        }

        private void toggle() {
            mAppDetailSafeDesContainer.measure(0, 0);
            int oldHeight = mAppDetailSafeDesContainer.getMeasuredHeight();
            if (isOpen) {
                //关闭
                valueAnimator(oldHeight, 0);
                rotateAnim(180, 360);
            } else {
                //打开
                valueAnimator(0, oldHeight);
                rotateAnim(0, 180);
            }
            isOpen = !isOpen;
        }

        /**
         * 旋转动画
         */
        private void rotateAnim(int start, int end) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mAppDetailSafeIvArrow, "rotation", start, end);
            objectAnimator.setDuration(500);
            objectAnimator.start();
        }

        /**
         * 展开动画
         */
        private void valueAnimator(int start, int end) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams layoutParams = mAppDetailSafeDesContainer.getLayoutParams();
                    Float animatedValue = (Float) animation.getAnimatedValue();
                    layoutParams.height = animatedValue.intValue();
                    mAppDetailSafeDesContainer.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.setDuration(500);
            valueAnimator.start();
        }

        public void setData(DetailBean detailBean) {
            List<DetailBean.SafeBean> safeBeanList = detailBean.safe;
            for (DetailBean.SafeBean safeBean : safeBeanList) {
                ImageView imageView = new ImageView(UiUtil.getContext());
                Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + safeBean.safeUrl).into(imageView);
                mAppDetailSafePicContainer.addView(imageView);

                LinearLayout linearLayout = new LinearLayout(UiUtil.getContext());

                ImageView imageView2 = new ImageView(UiUtil.getContext());
                Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + safeBean.safeDesUrl).into(imageView2);
                linearLayout.addView(imageView2);

                TextView textView = new TextView(UiUtil.getContext());
                textView.setText(safeBean.safeDes);
                if (safeBean.safeDesColor == 0) {
                    textView.setTextColor(Color.parseColor("#c3c3c3"));
                } else {
                    textView.setTextColor(Color.parseColor("#ff9000"));
                }

                linearLayout.addView(textView);
                mAppDetailSafeDesContainer.addView(linearLayout);
            }
        }
    }

    class PicHolder {
        @BindView(R.id.app_detail_pic_iv_container)
        LinearLayout mAppDetailPicIvContainer;

        public PicHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(DetailBean detailBean) {
            List<String> stringList = detailBean.screen;
            for (String picUrl : stringList) {
                RatioLayout ratioLayout = new RatioLayout(UiUtil.getContext());
                ratioLayout.setMode(RatioLayout.MODE_HEIGHT);
                ratioLayout.setRatio(0.56f);
                ratioLayout.setPadding(2, 2, 2, 2);

                ImageView imageView = new ImageView(UiUtil.getContext());
                Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + picUrl).into(imageView);

                ratioLayout.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mAppDetailPicIvContainer.addView(ratioLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 320));
            }
        }
    }

    class DesHolder {
        @BindView(R.id.app_detail_des_tv_des)
        TextView mAppDetailDesTvDes;
        @BindView(R.id.app_detail_des_tv_author)
        TextView mAppDetailDesTvAuthor;
        @BindView(R.id.app_detail_des_iv_arrow)
        ImageView mAppDetailDesIvArrow;
        private boolean isOpen;
        private int mWidth;

        public DesHolder(View view) {
            ButterKnife.bind(this, view);

            mAppDetailDesTvDes.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mWidth = mAppDetailDesTvDes.getMeasuredWidth();
                    Log.d(TAG, "onGlobalLayout: mWidth:" + mWidth);
                }
            });

            //设置初始状态值
            ViewGroup.LayoutParams layoutParams = mAppDetailDesTvDes.getLayoutParams();
            layoutParams.height = getSevenLinesHeight();
            mAppDetailDesTvDes.setLayoutParams(layoutParams);
            //设置点击监听
            mAppDetailDesIvArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            });
        }

        private int getSevenLinesHeight() {
            TextView textView = new TextView(UiUtil.getContext());
            textView.setText(mAppDetailDesTvDes.getText());
            textView.setLines(7);
            textView.measure(View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY), 0);
            int measuredHeight = textView.getMeasuredHeight();
            return measuredHeight;
        }

        private void toggle() {
            mAppDetailDesTvDes.measure(View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY), 0);
            int oldHeight = mAppDetailDesTvDes.getMeasuredHeight();

            if (isOpen) {
                //关闭
                valueAnimator(oldHeight, getSevenLinesHeight(), false);
                rotateAnim(180, 360);
            } else {
                //打开
                valueAnimator(getSevenLinesHeight(), oldHeight, true);
                rotateAnim(0, 180);
            }
            isOpen = !isOpen;
        }

        /**
         * 旋转动画
         */
        private void rotateAnim(int start, int end) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mAppDetailDesIvArrow, "rotation", start, end);
            objectAnimator.setDuration(500);
            objectAnimator.start();
        }

        /**
         * 展开动画
         *
         * @param start
         * @param end
         * @param isAddListener 是否需要添加addListener监听
         */
        private void valueAnimator(int start, int end, boolean isAddListener) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float animatedValue = (Float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mAppDetailDesTvDes.getLayoutParams();
                    layoutParams.height = animatedValue.intValue();
                    mAppDetailDesTvDes.setLayoutParams(layoutParams);
                }
            });
            if (isAddListener) {
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //拿到父布局
                        ViewParent parent = mAppDetailDesTvDes.getParent();
                        while (parent instanceof ViewGroup) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            if (viewGroup instanceof ScrollView) {
                                //如果是scrollview就滚动到最底下
                                ((ScrollView) viewGroup).fullScroll(View.FOCUS_DOWN);
                            }
                            parent = parent.getParent();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
            valueAnimator.setDuration(500);
            valueAnimator.start();
        }

        public void setData(DetailBean detailBean) {
            mAppDetailDesTvDes.setText(detailBean.des);
            mAppDetailDesTvAuthor.setText(detailBean.author);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mDetailBean != null) {
            if(mBottomHolder != null) {
                mBottomHolder.mState = DownloadManager.getInstance().getState(mDetailBean.packageName, mDetailBean.size);
                mBottomHolder.refreshUiByState(mBottomHolder.mState);
            }
        }
    }

    class BottomHolder implements DownloadManager.DownloadListener {
        @BindView(R.id.app_detail_download_btn_favo)
        Button mAppDetailDownloadBtnFavo;
        @BindView(R.id.app_detail_download_btn_share)
        Button mAppDetailDownloadBtnShare;
        @BindView(R.id.app_detail_download_btn_download)
        Button mBtnDownload;
        private DetailBean mDetailBean;
        private int mState;
        private long mProgress;

        public BottomHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.app_detail_download_btn_download)
        public void download(View v) {
            switch (mState) {
                case DownloadManager.STATE_UNDOWNLOAD:
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
            String pkgName = mDetailBean.packageName;
            if(CommonUtils.isInstalled(UiUtil.getContext(), pkgName)){
                //如果已经安装，则打开
                CommonUtils.openApp(UiUtil.getContext(), pkgName);
            }
        }

        private void install() {
            String pkgName = mDetailBean.packageName;
            //如果没有安装，则安装
            if(!CommonUtils.isInstalled(UiUtil.getContext(), pkgName)) {
                CommonUtils.installApp(UiUtil.getContext(),
                        DownloadManager.getInstance().getDownloadFile(pkgName));
            }
        }
        /**暂停*/
        private void pause() {
            DownloadManager.getInstance().pause(mDetailBean.packageName);
        }

        /**取消*/
        private void cancel() {
            DownloadManager.getInstance().cancel(mDetailBean.packageName);
            mState = DownloadManager.STATE_UNDOWNLOAD;
            refreshUiByState(mState);
        }

        private void downloadApk() {
            //下载应用
            DownloadManager.getInstance().download(mDetailBean, this);
        }

        public void setData(DetailBean detailBean) {
            mDetailBean = detailBean;

            mState = DownloadManager.getInstance().getState(detailBean.packageName, detailBean.size);
            refreshUiByState(mState);
        }

        @Override
        public void onStateChange(String packageName, final int state) {
            Log.d(TAG, "onStateChange: 监听状态：" + state);
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
                    mBtnDownload.setText("下载");
                    break;
                case DownloadManager.STATE_WAITING:
                    mBtnDownload.setText("正在等待,点击取消");
                    break;
                case DownloadManager.STATE_DOWNLOADING:
                    int precent = (int) (mProgress * 100f / mDetailBean.size);
                    mBtnDownload.setText(precent + "%");
                    break;
                case DownloadManager.STATE_PAUSE:
                    mBtnDownload.setText("继续下载");
                    break;
                case DownloadManager.STATE_SUCCESS:
                    mBtnDownload.setText("安装");
                    break;
                case DownloadManager.STATE_ERROR:
                    mBtnDownload.setText("重试");
                    break;
                case DownloadManager.STATE_INSTALLED:
                    mBtnDownload.setText("打开");
                    break;
            }
        }
    }
}
