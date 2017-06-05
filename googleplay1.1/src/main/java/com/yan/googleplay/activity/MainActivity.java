package com.yan.googleplay.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.yan.googleplay.R;
import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.manager.FragmentFactory;
import com.yan.googleplay.util.UiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tablayout)
    TabLayout mTablayout;
    @BindView(R.id.vp)
    ViewPager mVp;
    @BindView(R.id.drawer)
    DrawerLayout mDrawer;

    private String[] mTitles;
    /**是否为第一次加载*/
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initDrawer();
        initViewPager();
    }

    private void initViewPager() {
        mTitles = UiUtil.getStringArray(R.array.pagers);
        mVp.setAdapter(mFragmentStatePagerAdapter);

        mTablayout.setupWithViewPager(mVp);
        mVp.addOnPageChangeListener(mOnPageChangeListener);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            BaseFragment baseFragment = (BaseFragment) FragmentFactory.createFragmentByPosition(position);
            baseFragment.loadData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };

    private FragmentStatePagerAdapter mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        /**页面缓冲完成再进行加载首页*/
        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            if(isFirstLoad) {
                //只在第一次进入应用的时候创建一次
                mOnPageChangeListener.onPageSelected(0);
                isFirstLoad = false;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: " + position);

            return FragmentFactory.createFragmentByPosition(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }
    };

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
    }

}
