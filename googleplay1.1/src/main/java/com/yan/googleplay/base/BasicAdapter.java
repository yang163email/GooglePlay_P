package com.yan.googleplay.base;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yan.googleplay.R;
import com.yan.googleplay.holder.BasicHolder;
import com.yan.googleplay.holder.LoadMoreHolder;
import com.yan.googleplay.manager.ThreadManager;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;

import java.util.List;

/**
 * Created by 楠GG on 2017/5/27.
 */

public abstract class BasicAdapter<T> extends BaseAdapter {
    private static final String TAG = "BasicAdapter";
    private List<T> mData;
    private int layoutResId;
    private int layoutResId2;
    /**正常条目view类型*/
    public static final int TYPE_NORMAL = 0;
    /**加载更多条目View类型*/
    public static final int TYPE_LOAD_MORE = 1;
    public static final int TYPE_TITLE = 2;

    public BasicAdapter(List<T> data, int layoutResId) {
        mData = data;
        this.layoutResId = layoutResId;
    }

    public BasicAdapter(List<T> data, int layoutResId1, int layoutResId2) {
        mData = data;
        this.layoutResId = layoutResId1;
        this.layoutResId2 = layoutResId2;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getCount() - 1) {
            return TYPE_LOAD_MORE;
        }else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getCount() {
            return mData.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BasicHolder holder;
        if (convertView == null) {
            if (position == getCount() - 1) {
                //加载更多
                convertView = LayoutInflater.from(UiUtil.getContext()).inflate(R.layout.item_load_more, null);
                holder = getLoadMoreHolder(convertView);
            } else {
                if(getItemViewType(position) == TYPE_NORMAL) {
                    convertView = LayoutInflater.from(UiUtil.getContext()).inflate(layoutResId, null);
                }else {
                    convertView = LayoutInflater.from(UiUtil.getContext()).inflate(layoutResId2, null);
                }
                holder = createBasicHolder(convertView, position);
            }
            convertView.setTag(holder);
        } else {
            holder = (BasicHolder) convertView.getTag();
        }
//        holder.setData((TempAppBean) mData.get(position));
        if(position == getCount() - 1) {
            if(SupportLoadMore()) {
                //显示加载更多数据
                loadMore(convertView);
            }else {
                //隐藏加载更多
                getLoadMoreHolder(convertView).setData(LoadMoreHolder.STATE_NONE);
            }
        }else {
            onBindViewHolder(holder, position);
        }
        return convertView;
    }

    protected boolean SupportLoadMore() {
        return false;
    }

    public void loadMore(View convertView) {
        //判断是否已经在加载
        if(isLoadingMore) {
            return;
        }
        isLoadingMore = true;
        //初始化数据显示
        int state = LoadMoreHolder.STATE_LOADING;
        getLoadMoreHolder(convertView).setData(state);
        Log.d(TAG, "loadMore: ");
//        new Thread(new LoadMoreTask()).start();
        ThreadManager.getNormalPool().execute(new LoadMoreTask());
    }

    /**是否正在加载更多数据*/
    private boolean isLoadingMore;

    private class LoadMoreTask implements Runnable {

        @Override
        public void run() {
            int state = LoadMoreHolder.STATE_LOADING;
            List<T> moreDatas = null;
            try {
                //获取更多数据
                moreDatas = getMoreDataFromServer();
                if(moreDatas.size() < Constant.PAGE_SIZE) {
                    //最后一页
                    state = LoadMoreHolder.STATE_NONE;
                }
                SystemClock.sleep(Constant.SLEEP_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                state = LoadMoreHolder.STATE_RETRY;
            }

            if(moreDatas != null) {
                mData.addAll(moreDatas);
            }else {
                //服务器代码有错误
            }

            final int finalState = state;
            UiUtil.postTask(new Runnable() {
                @Override
                public void run() {
                    if(finalState != LoadMoreHolder.STATE_RETRY) {
                        notifyDataSetChanged();
                    }
                    mLoadMoreHolder.setData(finalState);
                    isLoadingMore = false;
                }
            });
        }
    }

    protected abstract List<T> getMoreDataFromServer() throws Exception;

    private LoadMoreHolder mLoadMoreHolder;

    private LoadMoreHolder getLoadMoreHolder(View convertView) {
        if(mLoadMoreHolder == null) {
            mLoadMoreHolder = new LoadMoreHolder(convertView);
            mLoadMoreHolder.setAdapter(this);
        }
        return mLoadMoreHolder;
    }

    /**
     * 子类实现方法创建一个BasicHolder
     */
    protected abstract BasicHolder createBasicHolder(View convertView, int position);

    /**
     * 让子类自己设置数据
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindViewHolder(BasicHolder holder, int position);

}
