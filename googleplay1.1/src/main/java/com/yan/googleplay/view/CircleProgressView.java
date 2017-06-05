package com.yan.googleplay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class CircleProgressView
        extends View
{

    // 显示 行为 状态 状态码
    // 下载 去下载 未下载状态 0
    // 等待中 取消下载 等待状态 1
    // 下载的进度 暂停下载 下载中状态 2
    // 继续下载 去下载 暂停状态 3
    // 安装 去安装 下载完成 4
    // 打开 去打开 安装完成 5
    // 重试 去下载 下载失败 6

    private int   mSize;
    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private Paint mPaint;

    private State mState = State.DOWNLOAD;

    private Path mDownloadPath;
    private int mWaitingFlag = 0;
    private int mProgress    = 0;
    private int mMax;

    private Path mPausePath;
    private Path mRetryPath;

    private float mCircleWidth;
    private int mCircleColor = 0xff0076CA;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize  = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mSize = widthSize > heightSize
                ? heightSize
                : widthSize;
        mCenterX = mSize / 2f;
        mCenterY = mSize / 2f;
        mRadius = mSize / 2f;

        mCircleWidth = mSize / 32f;

        setMeasuredDimension(mSize, mSize);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public void setState(State state) {
        this.mState = state;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                                                      Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        int padding = getPaddingTop();

        //1.外圈
        mPaint.reset();
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        canvas.drawCircle(mCenterX, mCenterY, mRadius - padding, mPaint);

        switch (mState) {
            case DOWNLOAD:
                drawDownload(canvas);
                break;
            case WAITING:
                drawWaiting(canvas);
                break;
            case LOADING:
                drawProgress(canvas);
                break;
            case PAUSE:
                drawPause(canvas);
                break;
            case OPEN:
                drawOpen(canvas);
                break;
            case RETRY:
                drawRetry(canvas);
                break;
        }
    }

    private void drawDownload(Canvas canvas) {
        //重置画笔
        mPaint.reset();
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        if (mDownloadPath == null) {
            float width = mSize / 6f;
            float height = mSize / 6f;
            mDownloadPath = new Path();
            float left = (mSize - width) / 2f;
            float top = (mSize - height * 2) / 2f;
            float right = left + width;
            float bottom = top + height + height / 4f;
            mDownloadPath.addRect(left, top, right, bottom, Path.Direction.CW);

            Path tra = new Path();
            float x1 = (mSize - width * 2) / 2f;
            float y1 = bottom;
            float x2 = mCenterX;
            float y2 = bottom + height;
            float x3 = x1 + width * 2;
            float y3 = bottom;
            tra.moveTo(x1, y1);
            tra.lineTo(x2, y2);
            tra.lineTo(x3, y3);
            tra.lineTo(x1, y1);

            mDownloadPath.addPath(tra);

            mDownloadPath.close();
        }
        canvas.drawPath(mDownloadPath, mPaint);
    }

    private void drawWaiting(Canvas canvas) {
        //重置画笔
        mPaint.reset();
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        canvas.drawCircle(mSize / 4f,
                          mRadius,
                          mWaitingFlag == 0
                          ? mSize / 12f
                          : mSize / 16f,
                          mPaint);
        canvas.drawCircle(mSize / 2f,
                          mRadius,
                          mWaitingFlag == 1
                          ? mSize / 12f
                          : mSize / 16f,
                          mPaint);
        canvas.drawCircle(mSize * 3 / 4f,
                          mRadius,
                          mWaitingFlag == 2
                          ? mSize / 12f
                          : mSize / 16f,
                          mPaint);
        if (mWaitingFlag == 2) {
            mWaitingFlag = 0;
        } else {
            mWaitingFlag++;
        }
        postInvalidateDelayed(200);
    }

    private void drawProgress(Canvas canvas) {
        //画笔重置
        mPaint.reset();
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(mSize / 16f));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        float sweepAngle = 0;
        if (mMax == 0) {
            sweepAngle = mProgress * 360f / 100f;
        } else {
            sweepAngle = mProgress * 360f / mMax;
        }


        //draw progress
        int padding = getPaddingTop();
        canvas.drawArc(new RectF(padding + mCircleWidth,
                                 padding + mCircleWidth,
                                 mSize - padding - mCircleWidth,
                                 mSize - padding - mCircleWidth), -90, sweepAngle, false, mPaint);

        //画笔重置
        mPaint.reset();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        float size   = mSize / 3f;//宽度为1/4
        float left   = (mSize - size) / 2f;
        float top    = (mSize - size) / 2f;
        float right  = left + size;
        float bottom = top + size;
        float rx     = size / 8f;
        float ry     = size / 8f;
        canvas.drawRoundRect(new RectF(left, top, right, bottom), rx, ry, mPaint);
    }

    private void drawPause(Canvas canvas) {
        //画笔重置
        mPaint.reset();
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        if (mPausePath == null) {
            mPausePath = new Path();

            float radius = mSize / 4f;
            float w = (float) (radius * Math.sin(Math.toRadians(30)));
            float h = (float) (radius * Math.sin(Math.toRadians(60)));

            float x1 = mRadius - w;
            float y1 = mRadius - h;
            float x2 = x1;
            float y2 = y1 + 2f * h;
            float x3 = x1 + w + radius;
            float y3 = y1 + h;

            mPausePath.moveTo(x1, y1);
            mPausePath.lineTo(x2, y2);
            mPausePath.lineTo(x3, y3);
            mPausePath.lineTo(x1, y1);
            mPausePath.close();
        }

        canvas.drawPath(mPausePath, mPaint);
    }

    private void drawOpen(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90, mCenterX, mCenterY);
        drawDownload(canvas);
        canvas.restore();
    }

    private void drawRetry(Canvas canvas) {
        float lineSize = dp2px(mSize / 32f);

        //画笔重置
        mPaint.reset();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineSize);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        if (mRetryPath == null) {
            mRetryPath = new Path();

            //arc
            float width = mSize / 2f;
            float height = mSize / 2f;
            float left = (mSize - width) / 2f;
            float top = (mSize - height) / 2f;
            float right = left + width;
            float bottom = top + height;
            mRetryPath.addArc(new RectF(left, top, right, bottom), 45, 315);

            //arrow
            Path arrow = new Path();
            float w = lineSize * 1.2f;
            float h = lineSize * 0.8f;

            float x1 = (right - lineSize + (w - lineSize) * 1.5f);
            float y1 = mRadius;
            float x2 = x1 + w / 2f;
            float y2 = y1 + h;
            float x3 = x1 + w;
            float y3 = y1;

            arrow.moveTo(x1, y1);
            arrow.lineTo(x2, y2);
            arrow.lineTo(x3, y3);
            arrow.lineTo(x1, y1);
            arrow.close();

            //            canvas.drawLine(right,0,right,300,mPaint);

            mRetryPath.addPath(arrow);
            mRetryPath.close();
        }

        canvas.drawPath(mRetryPath, mPaint);
    }


    private float dp2px(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public enum State {
        DOWNLOAD,
        WAITING,
        LOADING,
        PAUSE,
        OPEN,
        RETRY
    }
}
