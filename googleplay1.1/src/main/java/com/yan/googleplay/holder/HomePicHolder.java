package com.yan.googleplay.holder;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.yan.googleplay.R;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class HomePicHolder extends BasicHolder {
    @BindView(R.id.item_home_picture_pager)
    ViewPager mItemHomePicturePager;
    @BindView(R.id.item_home_picture_container_indicator)
    LinearLayout mItemHomePictureContainerIndicator;
    private List<String> mPics;

    public HomePicHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public void setData(List<String> pics) {
        this.mPics = pics;
        mItemHomePicturePager.setAdapter(new HomePicPagerAdapter());
        mItemHomePicturePager.setPageTransformer(true, new ZoomOutPageTransformer());
        int length = mPics.size();
        //添加原点指示器
        for (int i = 0; i < length; i++) {
            View dot = new View(UiUtil.getContext());
            if (i == 0) {
                //设置初始选中的点
                dot.setBackgroundResource(R.drawable.indicator_selected);
            } else {
                dot.setBackgroundResource(R.drawable.indicator_normal);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            params.leftMargin = 8;
            params.bottomMargin = 8;
            mItemHomePictureContainerIndicator.addView(dot, params);
        }

        mItemHomePicturePager.addOnPageChangeListener(mOnPageChangeListener);

        //设置轮播图的起始位置
        int startItem = Integer.MAX_VALUE / 2;
        startItem -= startItem % mPics.size();
        mItemHomePicturePager.setCurrentItem(startItem);

        final AutoScrollTask autoScrollTask = new AutoScrollTask();
        autoScrollTask.start();
        mItemHomePicturePager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        autoScrollTask.stop();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        autoScrollTask.start();
                        break;
                }
                return false;
            }
        });
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            position = position % mPics.size();
            int length = mPics.size();
            for (int i = 0; i < length; i++) {
                View dot = mItemHomePictureContainerIndicator.getChildAt(i);
                if (i == position) {
                    dot.setBackgroundResource(R.drawable.indicator_selected);
                } else {
                    dot.setBackgroundResource(R.drawable.indicator_normal);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class HomePicPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % mPics.size();
            ImageView imageView = new ImageView(UiUtil.getContext());
            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + mPics.get(position)).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 自动滚动的任务
     */
    private class AutoScrollTask implements Runnable {
        /**
         * 开始任务
         */
        public void start() {
            UiUtil.postDelay(this, 2000);
        }

        /**
         * 停止任务
         */
        public void stop() {
            UiUtil.removeTask(this);
        }

        @Override
        public void run() {
            int currentItem = mItemHomePicturePager.getCurrentItem();
            mItemHomePicturePager.setCurrentItem(++currentItem);

            UiUtil.postDelay(this, 2000);
        }
    }
}
