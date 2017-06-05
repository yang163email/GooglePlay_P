package com.yan.googleplay.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yan.googleplay.R;
import com.yan.googleplay.base.BaseFragment;
import com.yan.googleplay.base.BasicAdapter;
import com.yan.googleplay.base.HeimaAsyncTask;
import com.yan.googleplay.bean.CategoryBean;
import com.yan.googleplay.bean.CategoryVo;
import com.yan.googleplay.holder.BasicHolder;
import com.yan.googleplay.protocol.CategoryProtocol;
import com.yan.googleplay.util.Constant;
import com.yan.googleplay.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 楠GG on 2017/6/1.
 */

public class CategoryFragment extends BaseFragment {
    private static final String TAG = "CategoryFragment";
    private List<CategoryBean> mDatas;

    @Override
    protected View onPostExcute() {
        ListView listView = new ListView(UiUtil.getContext());
        listView.setAdapter(new CategoryAdapter(mDatas, R.layout.item_category,
                R.layout.item_category_title));
        return listView;
    }

    @Override
    public HeimaAsyncTask.Result doInBackground() {
        CategoryProtocol categoryProtocol = new CategoryProtocol();
        try {
            List<CategoryVo> categoryVoList = categoryProtocol.loadData();
            mDatas = transfor(categoryVoList);
            if (mDatas == null || mDatas.size() == 0) {
                return HeimaAsyncTask.Result.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HeimaAsyncTask.Result.ERROR;
        }
        return HeimaAsyncTask.Result.SUCCESS;
    }

    /**
     * 将CategoryVo的Bean转换成CategoryBean
     */
    private List<CategoryBean> transfor(List<CategoryVo> categoryVoList) {
        List<CategoryBean> categoryBeenList = new ArrayList<>();

        for (int i = 0; i < categoryVoList.size(); i++) {
            CategoryVo categoryVo = categoryVoList.get(i);

            CategoryBean categoryTitleBean = new CategoryBean();
            categoryTitleBean.type = CategoryBean.TYPE_TITLE;
            categoryTitleBean.title = categoryVo.title;
            categoryBeenList.add(categoryTitleBean);

            List<CategoryVo.InfosBean> infos = categoryVo.infos;
            for (int j = 0; j < infos.size(); j++) {
                CategoryVo.InfosBean infosBean = infos.get(j);

                CategoryBean categoryBean = new CategoryBean();
                categoryBean.type = CategoryBean.TYPE_NORMAL;
                categoryBean.name1 = infosBean.name1;
                categoryBean.name2 = infosBean.name2;
                categoryBean.name3 = infosBean.name3;

                categoryBean.url1 = infosBean.url1;
                categoryBean.url2 = infosBean.url2;
                categoryBean.url3 = infosBean.url3;

                categoryBeenList.add(categoryBean);
            }
        }
        return categoryBeenList;
    }

    private class CategoryAdapter extends BasicAdapter {

        @Override
        public int getCount() {
            return super.getCount() - 1;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            CategoryBean categoryBean = mDatas.get(position);
            if (position == getCount() - 1) {
                return TYPE_LOAD_MORE;
            } else {
                if (categoryBean.type == CategoryBean.TYPE_TITLE) {
                    return TYPE_TITLE;
                } else {
                    return TYPE_NORMAL;
                }
            }
        }

        public CategoryAdapter(List data, int layoutResId1, int layoutResId2) {
            super(data, layoutResId1, layoutResId2);
        }

        @Override
        protected List getMoreDataFromServer() throws Exception {
            return null;
        }

        @Override
        protected BasicHolder createBasicHolder(View convertView, int position) {
            CategoryBean categoryBean = mDatas.get(position);
            if (categoryBean.type == CategoryBean.TYPE_TITLE) {
                return new CategoryTitleHolder(convertView);
            }
            return new CategoryHolder(convertView);
        }

        @Override
        protected void onBindViewHolder(BasicHolder holder, int position) {
//            Log.d(TAG, "onBindViewHolder: " + mDatas.get(position).type);
            CategoryBean categoryBean = mDatas.get(position);
            if (categoryBean.type == CategoryBean.TYPE_TITLE) {
                CategoryTitleHolder categoryTitleHolder = (CategoryTitleHolder) holder;
                categoryTitleHolder.setData(mDatas.get(position));
            } else {
                CategoryHolder categoryHolder = (CategoryHolder) holder;
                categoryHolder.setData(mDatas.get(position));
            }
        }
    }

    public class CategoryTitleHolder extends BasicHolder {
        @BindView(R.id.item_category_title)
        public TextView mItemCategoryTitle;

        public CategoryTitleHolder(View view) {
            ButterKnife.bind(this, view);
//            mItemCategoryTitle = (TextView) view.findViewById(R.id.item_category_title);
        }

        public void setData(CategoryBean categoryBean) {
            Log.d(TAG, "setData: mItemCategoryTitle" + mItemCategoryTitle);
            mItemCategoryTitle.setText(categoryBean.title + "");
        }
    }

    public class CategoryHolder extends BasicHolder {
        @BindView(R.id.item_category_icon_1)
        ImageView mItemCategoryIcon1;
        @BindView(R.id.item_category_name_1)
        TextView mItemCategoryName1;
        @BindView(R.id.item_category_item_1)
        LinearLayout mItemCategoryItem1;
        @BindView(R.id.item_category_icon_2)
        ImageView mItemCategoryIcon2;
        @BindView(R.id.item_category_name_2)
        TextView mItemCategoryName2;
        @BindView(R.id.item_category_item_2)
        LinearLayout mItemCategoryItem2;
        @BindView(R.id.item_category_icon_3)
        ImageView mItemCategoryIcon3;
        @BindView(R.id.item_category_name_3)
        TextView mItemCategoryName3;
        @BindView(R.id.item_category_item_3)
        LinearLayout mItemCategoryItem3;

        public CategoryHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setData(CategoryBean categoryBean) {
            mItemCategoryName1.setText(categoryBean.name1);
            mItemCategoryName2.setText(categoryBean.name2);
            mItemCategoryName3.setText(categoryBean.name3);

            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + categoryBean.url1)
                    .into(mItemCategoryIcon1);
            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + categoryBean.url2)
                    .into(mItemCategoryIcon2);
            Picasso.with(UiUtil.getContext()).load(Constant.IMAGE + categoryBean.url3)
                    .into(mItemCategoryIcon3);
        }
    }
}
