package com.my.linechart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者 ：赵鹏   时间：2019/2/28
 */
public class CustomLineChartView extends View {

    private Paint textPaint, linePaint, bitmapPaint;
    private int textSize = Utils.sp2px(getContext(), 10);
    private int labColor = Color.parseColor("#9299a5");
    private int textColor = Color.parseColor("#ff3b42");
    private int colorLine = Color.parseColor("#ff9da0");//坐标轴颜色
    private int colorBrokenLine = Color.parseColor("#f2f2f2");//虚线颜色
    private int contentColor = Color.parseColor("#33ff3b42");
    private int commonLineWidth = 1;
    private int lineWidth = Utils.dip2px(getContext(), 2);
    private int paddingTop = Utils.sp2px(getContext(), 10), paddingLeftAndRight = Utils.sp2px(getContext(), 15);
    private int mMaxNum, mMinNum, mIntervalNum, mStartNum;
    private static int LAND_NUM = 7, VER_NUM = 6;
    private int mWidth, mHeight;
    private int mSelectIndex = -1;
    private int intervalHeight = Utils.dip2px(getContext(), 20);
    private int chartHeight = Utils.dip2px(getContext(), 33);//图表到头部的距离
    private float verTextWidth, mOriginalX, mOriginalY, intervalWidth;
    private float mLastX, mSlideX, mAllSlideX, mMaxSildeX, mCurDownX, mCurDownY;
    private Bitmap bpNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_chart_spot1);
    private Bitmap bpSelected = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_chart_spot2);
    private boolean isMoveRight;

    private List<MyBean> mList = new ArrayList<>();

    public CustomLineChartView(Context context) {
        this(context, null);
    }

    public CustomLineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void init(){
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
    }

    public void setData(List<MyBean> list){
        mList.clear();
        mList.addAll(list);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < mList.size(); i++){
                    int num = Utils.switchIntValue(mList.get(i).getNum());
                    if(i == 0){
                        mMaxNum = mMinNum = num;
                    }else{
                        if(num > mMaxNum){
                            mMaxNum = num;
                        }
                        if(num < mMinNum){
                            mMinNum = num;
                        }
                    }
                }
                calculateIntervalNum();
                initXY();
                postInvalidate();
            }
        }, 200);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mList.size() > LAND_NUM){
                    mAllSlideX = mMaxSildeX;
                    postInvalidate();
                }
            }
        }, 300);
    }

    private void initXY(){
        float startNumWidth = textPaint.measureText(getNumStr(mStartNum));
        float endNumWidth = textPaint.measureText(getNumStr(mStartNum + mIntervalNum * VER_NUM));
        verTextWidth = startNumWidth > endNumWidth?startNumWidth:endNumWidth;
        mOriginalX = paddingLeftAndRight + verTextWidth + Utils.dip2px(getContext(), 9);
        mOriginalY = chartHeight + intervalHeight * VER_NUM;
        intervalWidth = (mWidth - mOriginalX - paddingLeftAndRight) / LAND_NUM;
        mMaxSildeX = (mList.size() - 1) * intervalWidth - (mWidth - mOriginalX - paddingLeftAndRight);
        for(int i = 0; i < mList.size(); i++){
            MyBean bean = mList.get(i);
            int curNum = Utils.switchIntValue(bean.getNum());
            int y;
            if(mIntervalNum == 0){
                y = 0;
            }else{
                y = (mStartNum + VER_NUM * mIntervalNum - curNum) * intervalHeight * VER_NUM / (VER_NUM * mIntervalNum) + chartHeight;
            }
            int x = (int) (mOriginalX + i * intervalWidth);
            bean.setPoint(new Point(x, y));
        }
    }

    private void calculateIntervalNum(){
        int diffNum = mMaxNum - mMinNum;
        int interval = diffNum / VER_NUM;
        if(interval > 1000){
            mIntervalNum = (interval / 1000 + 1) * 1000;
            mStartNum = mMinNum / 1000 * 1000;
        }else if(interval > 100){
            mIntervalNum = (interval / 100 + 1) * 100;
            mStartNum = mMinNum / 100 * 100;
        }else if(interval > 10){
            mIntervalNum = (interval / 10 + 1) * 10;
            mStartNum = mMinNum / 10 * 10;
        }else if(interval > 1){
            mIntervalNum = interval;
            mStartNum = mMinNum;
        }
    }

    private String getNumStr(int num){
        if(mIntervalNum >= 1000){
            return String.valueOf(num / 1000) + "K";
        }else{
            return String.valueOf(num);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mList.size() <= 0){
            return;
        }
        if(mSelectIndex >= 0){
            String title = getContext().getResources().getString(R.string.title_line_chart,
                    Utils.stringYMD2(mList.get(mSelectIndex).getDate()), mList.get(mSelectIndex).getNum(), mList.get(mSelectIndex).getRate());
            Rect startRect = new Rect();
            textPaint.getTextBounds(getNumStr(mStartNum), 0, 1, startRect);
            textPaint.setColor(textColor);
            canvas.drawText(title, (mWidth - textPaint.measureText(title)) / 2, paddingTop + startRect.height(), textPaint);
        }

        //竖坐标轴标签
        canvas.save();
        Rect startRect = new Rect();
        textPaint.setColor(labColor);
        textPaint.getTextBounds(getNumStr(mStartNum), 0, 1, startRect);
        for(int i = 0; i <= VER_NUM; i++){
            canvas.drawText(getNumStr(mStartNum + mIntervalNum * (VER_NUM - i)), paddingLeftAndRight, chartHeight + i * intervalHeight, textPaint);
        }
        canvas.restore();

        //坐标轴
        canvas.save();
        linePaint.setStrokeWidth(commonLineWidth);
        linePaint.setColor(colorLine);
        canvas.drawLine(mOriginalX, chartHeight,
                mOriginalX, mOriginalY, linePaint);
        canvas.drawLine(mOriginalX, mOriginalY,
                mWidth - paddingLeftAndRight, mOriginalY, linePaint);
        canvas.restore();
        //画虚线
        canvas.save();
        linePaint.setPathEffect(new DashPathEffect(new float[] {10f, 5f}, 0));
        linePaint.setColor(colorBrokenLine);
        for(int i = 0; i < VER_NUM; i++){
            canvas.drawLine(mOriginalX, chartHeight + intervalHeight * i,
                    mWidth - paddingLeftAndRight, chartHeight + intervalHeight * i, linePaint);
        }
        canvas.restore();

        drawContent(canvas);

        drawDate(canvas);
    }

    private void drawContent(Canvas canvas) {
        canvas.save();
        canvas.clipRect(mOriginalX, chartHeight, mWidth - paddingLeftAndRight, chartHeight + intervalHeight * VER_NUM);
        //内容阴影
        Path path = new Path();
        path.moveTo(mOriginalX, mOriginalY);
        for(int i = 0; i < mList.size(); i++){
            path.lineTo(mList.get(i).getPoint().x, mList.get(i).getPoint().y);
        }
        path.lineTo(mList.get(mList.size() - 1).getPoint().x, mOriginalY);
        path.close();
        linePaint.setColor(contentColor);
        linePaint.setPathEffect(null);
        linePaint.setStyle(Paint.Style.FILL);
        canvas.translate(-mAllSlideX, 0);
        canvas.drawPath(path, linePaint);
        canvas.translate(mAllSlideX, 0);

        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(textColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        path.reset();
        for(int i = 0; i < mList.size(); i++){
            if(i == 0){
                path.moveTo(mList.get(i).getPoint().x, mList.get(i).getPoint().y);
            }else{
                path.lineTo(mList.get(i).getPoint().x, mList.get(i).getPoint().y);
            }
        }
        canvas.translate(-mAllSlideX, 0);
        canvas.drawPath(path, linePaint);
        canvas.translate(mAllSlideX, 0);

        if(mSelectIndex > 0){
            linePaint.setStrokeWidth(1);
            linePaint.setColor(textColor);
            linePaint.setStyle(Paint.Style.STROKE);
            canvas.translate(-mAllSlideX, 0);
            canvas.drawLine(mList.get(mSelectIndex).getPoint().x, mOriginalY, mList.get(mSelectIndex).getPoint().x, chartHeight, linePaint);
            canvas.translate(mAllSlideX, 0);
        }

        for(int i = 0; i < mList.size(); i++){
            int x = mList.get(i).getPoint().x;
            int y = mList.get(i).getPoint().y;

            canvas.translate(-mAllSlideX, 0);
            if(mSelectIndex == i){
                canvas.drawBitmap(bpSelected, x - bpSelected.getWidth() / 2, y - bpSelected.getHeight() / 2, bitmapPaint);
            }else{
                canvas.drawBitmap(bpNormal, x - bpNormal.getWidth() / 2, y - bpNormal.getHeight() / 2, bitmapPaint);
            }
            canvas.translate(mAllSlideX, 0);
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curX = event.getX();
        float silde;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mCurDownX = mLastX = event.getX();
                mCurDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                silde = mLastX - curX;
                if(silde > 0){
                    isMoveRight = true;
                }else{
                    isMoveRight = false;
                }
                if(mAllSlideX + silde >= 0 && mAllSlideX + silde <= mMaxSildeX){
                    mSlideX = silde;
                    mAllSlideX += mSlideX;
                    postInvalidate();
                }
                mLastX = curX;
                break;
            case MotionEvent.ACTION_UP:
                silde = mLastX - curX;
                if(Math.abs(event.getX() - mCurDownX) < 20 && Math.abs(event.getY() - mCurDownY) < 20){//点击事件
                    checkClickPostion(event);
                    postInvalidate();
                }else if(mAllSlideX + silde >= 0 && mAllSlideX + silde <= mMaxSildeX){
                    mSlideX = silde;
                    mAllSlideX += mSlideX;
                    int num = (int) (mAllSlideX / intervalWidth);
                    float endX;
                    if(isMoveRight){
                        endX = (num + 1) * intervalWidth;
                    }else{
                        endX = num * intervalWidth;
                    }
                    if(endX >= 0 && endX <= mMaxSildeX){
                        smoothScroll(mAllSlideX, endX);
                    }
                    postInvalidate();
                }
                mLastX = curX;
                break;
        }
        return true;
    }

    private void checkClickPostion(MotionEvent event){
        float clickX = event.getX();
        float clickY = event.getY();
        for(int i = 0; i < mList.size(); i++){
            float x = mList.get(i).getPoint().x - mAllSlideX;
            float y = mList.get(i).getPoint().y;
            if(Math.abs(x - clickX) < 20 && Math.abs(y - clickY) < 20){
                if(mSelectIndex == i){
                    mSelectIndex = -1;
                }else{
                    mSelectIndex = i;
                }
            }
        }
    }

    private void smoothScroll(float start, float end){
        ValueAnimator v = ValueAnimator.ofFloat(start, end);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAllSlideX = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        v.setDuration(200);
        v.start();
    }

    private void drawDate(Canvas canvas){
        if(mAllSlideX / intervalWidth >= 0 && mList.size() > (mAllSlideX / intervalWidth + 7)){
            long endDate = Utils.switchLongValue(mList.get((int) (mAllSlideX / intervalWidth + 7)).getDate());
            long startDate = Utils.switchLongValue(mList.get((int) (mAllSlideX / intervalWidth)).getDate());
            textPaint.setColor(textColor);
            canvas.drawText(Utils.formatTimeToMD(startDate), mOriginalX, mOriginalY + Utils.dip2px(getContext(), 14), textPaint);
            canvas.drawText(Utils.formatTimeToMD(endDate), mWidth - paddingLeftAndRight - textPaint.measureText(Utils.formatTimeToMD(endDate)), mOriginalY + Utils.dip2px(getContext(), 14), textPaint);
        }
    }
}
