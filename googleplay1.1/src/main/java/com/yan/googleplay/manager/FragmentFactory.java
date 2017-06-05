package com.yan.googleplay.manager;

import android.support.v4.app.Fragment;

import com.yan.googleplay.fragment.AppFragment;
import com.yan.googleplay.fragment.CategoryFragment;
import com.yan.googleplay.fragment.GameFragment;
import com.yan.googleplay.fragment.HomeFragment;
import com.yan.googleplay.fragment.HotFragment;
import com.yan.googleplay.fragment.OtherFragment;
import com.yan.googleplay.fragment.RecommendFragment;
import com.yan.googleplay.fragment.SubjectFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 楠GG on 2017/5/26.
 */

public class FragmentFactory {

    /**缓存Fragment*/
    private static Map<Integer, Fragment> sFragmentCache = new HashMap<>();
    private static FragmentFactory sFragmentFactory;

    private FragmentFactory() {}

    public static FragmentFactory getInstance() {
        if(sFragmentFactory == null) {
            sFragmentFactory = new FragmentFactory();
        }
        return sFragmentFactory;
    }

    /**创建Fragment*/
    public static Fragment createFragmentByPosition(int position) {
        if(sFragmentCache.containsKey(position)) {
            return sFragmentCache.get(position);
        }
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new AppFragment();
                break;
            case 2:
                fragment = new GameFragment();
                break;
            case 3:
                fragment = new SubjectFragment();
                break;
            case 4:
                fragment = new RecommendFragment();
                break;
            case 5:
                fragment = new CategoryFragment();
                break;
            case 6:
                fragment = new HotFragment();
                break;
            default:
                fragment = new OtherFragment();
                break;
        }
        sFragmentCache.put(position, fragment);
        return fragment;
    }
}
