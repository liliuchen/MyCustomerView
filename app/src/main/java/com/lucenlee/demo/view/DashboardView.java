package com.lucenlee.demo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author liliuchen
 * @package com.lucenlee.demo.dashboardview
 * @fileName DashboardView
 * @date 2019/6/14
 * @emial 871898381@qq.com
 * @describe 仪表盘
 * @company
 */
public class DashboardView extends View {
    //底部标题
    private String mTitleText = "标题";
    //底部标题颜色
    private int mTitleTextColor = Color.BLACK;
    //底部标题字体大小
    private float mTitleTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
    //标题画笔
    private TextPaint mTitleTextPaint;
    private float mTitleTextWidth;
    private float mTitleTextHeight;

    //背景颜色
    private int mBgColor = Color.WHITE;
    //得分字体颜色
    private int mScoreTextColor = Color.RED;
    //得分字体大小
    private float mScoreTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

    //得分画笔
    private TextPaint mScoreTextPaint;
    private float mScoreTextWidth;
    private float mScoreTextHeight;
    private float mScoreTextShadow;

    //弧形半径
    private float mArcRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

    //弧形的颜色
    private int mArcColor = Color.BLUE;
    //弧形背景的颜色
    private int mArcBgColor = Color.LTGRAY;
    //弧形的宽度
    private float mArcWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    ;
    private TextPaint mArcPaint;
    //刻度的最大值
    private int mMax = 100;
    //刻度的最小值
    private int mMin = 0;
    //大刻度的数量
    private int mBigScaleCount = 10;
    //小刻度的数量
    private int mSmallScaleCount = 5;

    //刻度显示的位置，内部或者外部，默认内部
    private int mScaleTextType = 0;
    //刻度文字字体大小
    private float mScaleTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

    //得分画笔
    private TextPaint mScaleTextPaint;
    private float mScaleTextWidth;
    private float mScaleTextHeight;

    //刻度文字显示的位置与弧形的间距
    private float mScaleTextMargin = 5;
    //刻度的值小于当前值时刻度文字的颜色
    private int mLessCurrentValueScaleTextColor = Color.BLACK;

    //刻度的值大于当前值时刻度文字的颜色
    private int mMoreCurrentValueScaleTextColor = Color.GRAY;
    //当前值
    private int mCurrentValue = 50;

    //弧形绘制开始角度
    private float mArcAngleStart = 135;
    //弧形绘制结束角度
    private float mArcAngleEnd = mArcAngleStart+270;

    //动画时间毫秒
    private int mDuration = 1000;
    private float mDrawDegree;
    private String TAG = "DashboardView";
    private ValueAnimator mValueAnimator;
    private int mSection = 5;
    private float mCenterX, mCenterY; // 圆心坐标
    private String[] mTexts = new String[]{"0", "25", "50", "70", "80", "90", "99", "100"};
    private int mSingleAngle;

    public DashboardView(Context context) {
        super(context);
        init(null, 0);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DashboardView, defStyle, 0);
        //获取底部标题文字属性
        mTitleText = a.getString(
                R.styleable.DashboardView_dv_title_text);
        if (mTitleText == null) {
            mTitleText = "";
        }
        mTitleTextColor = a.getColor(
                R.styleable.DashboardView_dv_title_color,
                mTitleTextColor);

        mTitleTextSize = a.getDimension(
                R.styleable.DashboardView_dv_title_size,
                mTitleTextSize);
        //获取得分的文字属性

        mBgColor = a.getColor(
                R.styleable.DashboardView_dv_bg,
                mBgColor);
        mScoreTextColor = a.getColor(
                R.styleable.DashboardView_dv_score_color,
                mScoreTextColor);
        mScoreTextShadow = a.getDimension(
                R.styleable.DashboardView_dv_score_shadow,
                mScoreTextShadow);
        mScoreTextSize = a.getDimension(
                R.styleable.DashboardView_dv_score_size,
                mScoreTextSize);
        //获取刻度文字的文字属性
        mLessCurrentValueScaleTextColor = a.getColor(
                R.styleable.DashboardView_dv_scale_text_color_less,
                mLessCurrentValueScaleTextColor);
        mMoreCurrentValueScaleTextColor = a.getColor(
                R.styleable.DashboardView_dv_scale_text_color_more,
                mMoreCurrentValueScaleTextColor);
        mScaleTextSize = a.getDimension(
                R.styleable.DashboardView_dv_scale_text_size,
                mScaleTextSize);
        mScaleTextMargin = a.getDimension(
                R.styleable.DashboardView_dv_scale_text_margin,
                mScaleTextMargin);
        mScaleTextType = a.getInt(R.styleable.DashboardView_dv_scale_text_type, mScaleTextType);
        //获取最大值最小值
        mMax = a.getInt(
                R.styleable.DashboardView_dv_max,
                mMax);
        mMin = a.getInt(
                R.styleable.DashboardView_dv_min,
                mMin);
        mCurrentValue = a.getInt(
                R.styleable.DashboardView_dv_scale_current_value,
                mCurrentValue);


        mBigScaleCount = a.getInt(R.styleable.DashboardView_dv_scale_big, mBigScaleCount);

        mSmallScaleCount = a.getInt(R.styleable.DashboardView_dv_scale_small, mSmallScaleCount);

        //获取刻度开始的角度
        mArcAngleStart = a.getFloat(
                R.styleable.DashboardView_dv_angle_start,
                mArcAngleStart);
        mArcAngleEnd = a.getFloat(
                R.styleable.DashboardView_dv_angle_end,
                mArcAngleEnd);
        mDuration = a.getInt(
                R.styleable.DashboardView_dv_duration,
                mDuration);
        mArcRadius = a.getDimension(
                R.styleable.DashboardView_dv_arc_radius,
                mArcRadius);
        mArcWidth = a.getDimension(
                R.styleable.DashboardView_dv_arc_width,
                mArcWidth);
        mArcColor = a.getColor(
                R.styleable.DashboardView_dv_arc_color,
                mArcColor);
        a.recycle();


        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTitleTextPaint.setTextAlign(Paint.Align.LEFT);

        mScoreTextPaint = new TextPaint();
        mScoreTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mScoreTextPaint.setTextAlign(Paint.Align.LEFT);

        mScaleTextPaint = new TextPaint();
        mScaleTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mScaleTextPaint.setTextAlign(Paint.Align.LEFT);


        mArcPaint = new TextPaint();
        mArcPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        invalidateTextPaintAndMeasurements();
        if (mTexts == null) {
            mTexts = new String[mSection + 1]; // 需要显示mSection + 1个刻度读数
            for (int i = 0; i < mTexts.length; i++) {
                int n = (mMax - mMin) / mSection;
                mTexts[i] = String.valueOf(mMin + i * n);
            }
        } else {
            mSection = mTexts.length - 1;
        }
        mSingleAngle = (int) ((mArcAngleEnd - mArcAngleStart) / mSection);

        startAnim(0, mCurrentValue);
    }

    public int getIndexMaxValue(float value) {
        int index = 0;
        for (int i = 0; i < mTexts.length; i++) {
            if (Integer.parseInt(mTexts[i]) >= value) {
                index = i;
                break;
            }
        }
        return index;
    }

    public float valueToDegree(float value) {
        int indexMaxValue = getIndexMaxValue(value);
        if (indexMaxValue != 0) {
            if (indexMaxValue==mTexts.length-1){
                return mArcAngleEnd-mArcAngleStart;
            }
            float angle1 = mSingleAngle * (indexMaxValue - 1);
            float angle2 = mSingleAngle * (value - Integer.parseInt(mTexts[indexMaxValue - 1])) / (Integer.parseInt(mTexts[indexMaxValue]) - Integer.parseInt(mTexts[indexMaxValue - 1]));
            return (angle1 + angle2);
        } else {
            return 0;
        }
    }

    public float degreeToValue(float degree) {
        int count = (int) (degree / mSingleAngle);
        if (count != mTexts.length - 1) {
            float i =  (Integer.parseInt(mTexts[count + 1]) - Integer.parseInt(mTexts[count]))*(degree - count * mSingleAngle) / mSingleAngle ;
            return i + Integer.parseInt( mTexts[count]);
        } else {
            return Integer.parseInt(mTexts[mTexts.length - 1]);
        }
    }


    private void invalidateTextPaintAndMeasurements() {
        mTitleTextPaint.setTextSize(mTitleTextSize);
        mTitleTextPaint.setColor(mTitleTextColor);
        mTitleTextWidth = mTitleTextPaint.measureText(mTitleText);
        mTitleTextHeight = mTitleTextPaint.getFontMetrics().bottom;

        mScoreTextPaint.setTextSize(mScoreTextSize);
        mScoreTextPaint.setColor(mScoreTextColor);
        mScoreTextPaint.setShadowLayer(mScoreTextShadow, 0, 0, mScoreTextColor);
        mScaleTextPaint.setTextSize(mScaleTextSize);


        mArcPaint.setColor(mArcColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        int max = Math.min(width - getPaddingLeft() - getPaddingRight(), height - getPaddingTop() - getPaddingBottom());
        if (max < mArcRadius * 2) {
            mArcRadius = max / 2;
        }
        mCenterX = mCenterY = getMeasuredWidth() / 2f;

    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int realWidth;
        if (mode == MeasureSpec.EXACTLY) {
            realWidth = width;
        } else if (mode == MeasureSpec.AT_MOST) {
            realWidth = (int) (mArcRadius * 2) + getPaddingLeft() + getPaddingRight();
        } else {
            realWidth = width;
        }


        return realWidth;

    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int realHeight;
        if (mode == MeasureSpec.EXACTLY) {
            realHeight = height;
        } else if (mode == MeasureSpec.AT_MOST) {
            realHeight = (int) (mArcRadius * 2) + getPaddingBottom() + getPaddingTop();
        } else {
            realHeight = height;
        }

        return realHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.save();
        canvas.drawColor(mBgColor);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;


        RectF rectDraw = new RectF(paddingLeft, paddingTop, getWidth() - paddingRight, getHeight() - paddingBottom);
        RectF rectArc = new RectF(rectDraw.centerX() - mArcRadius, rectDraw.centerY() - mArcRadius, rectDraw.centerX() + mArcRadius, rectDraw.centerY() + mArcRadius);
        float startAngle = mArcAngleStart;

        mArcPaint.setColor(mArcBgColor);

        canvas.drawArc(rectArc, startAngle, mArcAngleEnd - mArcAngleStart, true, mArcPaint);
        mArcPaint.setColor(mArcColor);

        canvas.drawArc(rectArc, startAngle, mDrawDegree, true, mArcPaint);
        mArcPaint.setColor(mBgColor);
        canvas.drawCircle(rectArc.centerX(), rectArc.centerY(), mArcRadius - mArcWidth, mArcPaint);
        float value = degreeToValue(mDrawDegree);


        String valueInt = String.format("%.0f", value);
        mScoreTextWidth = mScoreTextPaint.measureText(valueInt);
        Paint.FontMetrics fontMetrics = mScoreTextPaint.getFontMetrics();
        Rect bounds = new Rect();
        mScoreTextPaint.getTextBounds(valueInt, 0, valueInt.length(), bounds);
        //计算长宽
        int x = getMeasuredWidth() / 2 - bounds.width() / 2;
        int y = (int) ((getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top);
        canvas.drawText(valueInt, x, y, mScoreTextPaint);
//        canvas.drawText(valueInt,
//                paddingLeft + (contentWidth - mScoreTextWidth) / 2,
//                paddingTop + (contentHeight + mScoreTextHeight) / 2,
//                mScoreTextPaint);


        float α;
        float[] p;
        float angle = (mArcAngleEnd - mArcAngleStart) * 1f / mSection;
        for (int i = 0; i <= mSection; i++) {
            α = mArcAngleStart + angle * i;
            p = getCoordinatePoint((int) (mArcRadius - mArcWidth  - mScaleTextMargin), α);
            if (α % 360 >= 135 && α % 360 <= 225) {
                mScaleTextPaint.setTextAlign(Paint.Align.LEFT);
            } else if ((α % 360 >= 0 && α % 360 <= 45) || (α % 360 >= 315 && α % 360 <= 360)) {
                mScaleTextPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                mScaleTextPaint.setTextAlign(Paint.Align.CENTER);
            }
            Rect mRectText = new Rect();
            mScaleTextPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);

            if (Integer.parseInt(mTexts[i]) > value) {
                mScaleTextPaint.setColor(mMoreCurrentValueScaleTextColor);
            } else {
                mScaleTextPaint.setColor(mLessCurrentValueScaleTextColor);
            }

            int txtH = mRectText.height();
            if (i <= 1 || i >= mSection - 1) {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH / 2, mScaleTextPaint);
            } else if (i == 3) {
                canvas.drawText(mTexts[i], p[0] + txtH / 2, p[1] + txtH, mScaleTextPaint);
            } else if (i == mSection - 3) {
                canvas.drawText(mTexts[i], p[0] - txtH / 2, p[1] + txtH, mScaleTextPaint);
            } else {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH, mScaleTextPaint);
            }
        }


        mTitleTextWidth = mTitleTextPaint.measureText(mTitleText);
        mTitleTextHeight = mTitleTextPaint.getFontMetrics().bottom;
        canvas.drawText(mTitleText + "",
                paddingLeft + (contentWidth - mTitleTextWidth) / 2,
                paddingTop + (contentHeight + mTitleTextHeight) / 2 + mArcRadius - mArcWidth,
                mTitleTextPaint);


    }

    public int getCurrentValue() {
        return mCurrentValue;
    }

    public void setCurrentValue(int currentValue) {
        Log.i(TAG, "setCurrentValue: " + currentValue);
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            float end = (float) mValueAnimator.getAnimatedValue();
            startAnim(degreeToValue(end), currentValue);
            mCurrentValue = currentValue;
            return;
        }
        startAnim(mCurrentValue, currentValue);
        mCurrentValue = currentValue;
    }

    public void startAnim(float startValue, float endValue) {

        float i = valueToDegree(endValue);
        float i1 = valueToDegree(startValue);
        mValueAnimator = ValueAnimator.ofFloat(i1, i);
        mValueAnimator.setDuration((long) mDuration);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mDrawDegree = (float) animation.getAnimatedValue();

                Log.i(TAG, "onAnimationUpdate: " + mDrawDegree);


                invalidate();
            }

        });

        mValueAnimator.start();

    }

    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }


}
