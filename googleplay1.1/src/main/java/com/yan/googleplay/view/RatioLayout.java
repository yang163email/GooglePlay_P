package com.yan.googleplay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yan.googleplay.R;

/**
 * Created by 楠GG on 2017/5/31.
 */

public class RatioLayout extends FrameLayout {
    private float mRatio;
    private int mode;
    public static final int MODE_WIDTH = 0;
    public static final int MODE_HEIGHT = 1;

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public RatioLayout(Context context) {
        this(context, null);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mRatio = typedArray.getFloat(R.styleable.RatioLayout_Ratio, 2.42f);
        mode = typedArray.getInteger(R.styleable.RatioLayout_Mode, MODE_WIDTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(mode == MODE_WIDTH) {
            //测量孩子
            int childWidth = widthSize - getPaddingLeft() - getPaddingRight();
            int childHeight = (int) (childWidth / mRatio);
            measureChildren(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));

            //设置自己尺寸
            setMeasuredDimension(widthSize, childHeight + getPaddingTop() + getPaddingBottom());
        }else if(mode == MODE_HEIGHT) {
            //测量孩子
            int childHeight = heightSize - getPaddingTop() - getPaddingBottom();
            int childWidth = (int) (childHeight * mRatio);
            measureChildren(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));

            //设置自己尺寸
            setMeasuredDimension(childWidth + getPaddingLeft() + getPaddingRight(), heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}