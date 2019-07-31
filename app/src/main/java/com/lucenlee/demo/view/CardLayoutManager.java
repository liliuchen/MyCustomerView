package com.lucenlee.demo.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author liliuchen
 * @package com.lucenlee.demo.inputview
 * @fileName CardLayoutManager
 * @date 2019/7/31
 * @emial 871898381@qq.com
 * @describe 卡片视图
 * @company
 */
public class CardLayoutManager extends RecyclerView.LayoutManager {

    private View mLeftViewPosition;
    private View mCenterViewForPosition;
    private View mRightViewPosition;
    private int mCurrentOffset = 0;
    private int mCurrentPosition = 0;
    private float mMinScale = 0.8f;
    private float mMinAlpha = 0.5f;

    public CardLayoutManager(float minScale, float minAlpha, int currentPosition) {
        mMinScale = minScale;
        mCurrentPosition = currentPosition;
        mMinAlpha = minAlpha;
    }

    public CardLayoutManager() {

    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        mCurrentPosition = position;
        this.requestLayout();

    }

    private boolean isScolling = false;

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
        if (isScolling) return;
        isScolling = true;
        smoothAnimator(recyclerView, state, position);


    }

    private void smoothAnimator(final RecyclerView recyclerView, final RecyclerView.State state, final int position) {


        if (mCurrentPosition != position) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothAnimator(recyclerView, state, position);
                }
            }, 300);

            if (position < mCurrentPosition) {
                recyclerView.smoothScrollBy(-mCurrentOffset, 0);
            } else {
                recyclerView.smoothScrollBy(mCurrentOffset, 0);
            }
        } else {
            isScolling = false;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果没有item，直接返回
        if (getItemCount() <= 0) return;
        // 跳过preLayout，preLayout主要用于支持动画
        if (state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        //下面计算左边需要显示的视图
        int childCount = getItemCount();
        int leftStart;
        if (mCurrentPosition - 2 >= 0) {
            leftStart = mCurrentPosition - 2;

        } else if (mCurrentPosition - 1 >= 0) {
            leftStart = mCurrentPosition - 1;
        } else {
            leftStart = mCurrentPosition;
        }
        for (int i = leftStart; i < mCurrentPosition; i++) {
            View view = recycler.getViewForPosition(i);
            if (i == mCurrentPosition - 1) {
                mLeftViewPosition = view;
            }
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int measuredWidth = getDecoratedMeasuredWidth(view);
            int measuredHeight = getDecoratedMeasuredHeight(view);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setAlpha(mMinAlpha);
            layoutDecorated(view, getPaddingLeft(), getPaddingTop(), getPaddingLeft() + measuredWidth, measuredHeight);

        }
        //计算右边需要显示视图
        int endRight;
        if (mCurrentPosition + 2 <= childCount - 1) {
            endRight = mCurrentPosition + 2;
        } else if (mCurrentPosition + 1 <= childCount - 1) {
            endRight = mCurrentPosition + 1;
        } else {
            endRight = mCurrentPosition;
        }
        for (int i = endRight; i > mCurrentPosition; i--) {

            View view = recycler.getViewForPosition(i);
            if (i == mCurrentPosition + 1) {
                mRightViewPosition = view;
            }
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int measuredWidth = getDecoratedMeasuredWidth(view);
            int measuredHeight = getDecoratedMeasuredHeight(view);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setAlpha(mMinAlpha);
            layoutDecorated(view, getWidth() - measuredWidth, getPaddingTop(), getWidth(), measuredHeight);
        }

        //绘制中间视图
        {
            mCenterViewForPosition = recycler.getViewForPosition(mCurrentPosition);
            addView(mCenterViewForPosition);
            mCenterViewForPosition.setAlpha(1);
            mCenterViewForPosition.setScaleX(1);
            mCenterViewForPosition.setScaleY(1);
            measureChildWithMargins(mCenterViewForPosition, 0, 0);
            int measuredWidth = getDecoratedMeasuredWidth(mCenterViewForPosition);
            int measuredHeight = getDecoratedMeasuredHeight(mCenterViewForPosition);
            mCurrentOffset = (getWidth() - measuredWidth) / 2;
            layoutDecorated(mCenterViewForPosition, (getWidth() - measuredWidth) / 2, getPaddingTop(), (getWidth() - measuredWidth) / 2 + measuredWidth, measuredHeight);
        }


    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        //向右滑动，但是已经到达最左端了，不做滑动处理了
        if (dx < 0 && mCurrentPosition == 0) {
            return 0;
        }
        //向左滑动，但是已经到达最右端了，不做滑动处理了
        if (dx > 0 && mCurrentPosition == getItemCount() - 1) {
            return 0;
        }

        mCurrentOffset -= dx;
        if (mCurrentOffset <= 0) {
            //中间视图达到左边界
            mCurrentOffset = 0;
            mCurrentPosition += 1;
            onLayoutChildren(recycler, state);
        } else if (mCurrentOffset >= getWidth() - mCenterViewForPosition.getMeasuredWidth()) {
            //中间视图达到右边界
            mCurrentOffset = getWidth() - mCenterViewForPosition.getMeasuredWidth();
            mCurrentPosition -= 1;
            onLayoutChildren(recycler, state);
        }
        //计算放大和透明度
        float scale, alpha;
        float space = (0.0f + getWidth() - mCenterViewForPosition.getMeasuredWidth()) / 2;
        if (mCurrentOffset < space) {
            //向左滑动，右端视图跟着的滑动
            scale = mMinScale + (1 - mMinScale) * mCurrentOffset / space;
            alpha = mMinAlpha + (1 - mMinAlpha) * mCurrentOffset / space;
            layoutRight((1 - mMinScale) * mCurrentOffset / space, (1 - mMinAlpha) * mCurrentOffset / space);
        } else {
            //向右滑动，左边视图跟着滑动
            scale = 1 - (1 - mMinScale) * (mCurrentOffset - space) / space;
            alpha = 1 - (1 - mMinAlpha) * (mCurrentOffset - space) / space;
            layoutLeft((1 - mMinScale) * (mCurrentOffset - space) / space, (1 - mMinAlpha) * (mCurrentOffset - space) / space);
        }
        layoutCenter(scale, alpha);
        return dx;
    }

    /**
     * 移动左端视图位置
     *
     * @param scale 放大和透明度偏移值
     */
    private void layoutLeft(float scale, float alpha) {
        mLeftViewPosition.setScaleY(mMinScale + scale);
        mLeftViewPosition.setScaleX(mMinScale + scale);
        mLeftViewPosition.setAlpha(mMinAlpha + alpha);
        layoutDecorated(mLeftViewPosition, mCurrentOffset - (getWidth() - mLeftViewPosition.getWidth()) / 2, getPaddingTop(), mCurrentOffset - (getWidth() - mLeftViewPosition.getWidth()) / 2 + mLeftViewPosition.getMeasuredWidth(), mLeftViewPosition.getMeasuredHeight());
    }

    /**
     * 移动右端视图位置
     *
     * @param scale 放大和透明度偏移值
     */
    private void layoutRight(float scale, float alpha) {

        mRightViewPosition.setScaleY(1 - scale);
        mRightViewPosition.setScaleX(1 - scale);
        mRightViewPosition.setAlpha(1 - alpha);
        int centerLeftToRightLeft = getWidth() - mCenterViewForPosition.getWidth() - (getWidth() - mCenterViewForPosition.getWidth()) / 2;
        layoutDecorated(mRightViewPosition, mCurrentOffset + centerLeftToRightLeft, getPaddingTop(), mCurrentOffset + centerLeftToRightLeft + mRightViewPosition.getMeasuredWidth(), mCenterViewForPosition.getMeasuredHeight());
    }


    /**
     * 移动中间视图位置
     *
     * @param scale 放大和透明度偏移值
     */
    private void layoutCenter(float scale, float alpha) {
        mCenterViewForPosition.setScaleY(scale);
        mCenterViewForPosition.setScaleX(scale);
        mCenterViewForPosition.setAlpha(alpha);
        layoutDecorated(mCenterViewForPosition, mCurrentOffset, getPaddingTop(), mCurrentOffset + mCenterViewForPosition.getMeasuredWidth(), mCenterViewForPosition.getMeasuredHeight());
    }

    @Override
    public void onAttachedToWindow(final RecyclerView view) {
        super.onAttachedToWindow(view);
        //监听recyclerView滑动停止，进行反弹和继续移动
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        int space = (getWidth() - mCenterViewForPosition.getWidth()) / 2;
                        if (mCurrentOffset < space / 2) {
                            recyclerView.smoothScrollBy(mCurrentOffset, 0);
                        } else if (mCurrentOffset < space) {
                            recyclerView.smoothScrollBy(-(space - mCurrentOffset), 0);
                        } else if (mCurrentOffset < space + space / 2) {
                            recyclerView.smoothScrollBy(mCurrentOffset - space, 0);
                        } else {
                            recyclerView.smoothScrollBy((mCurrentOffset - space * 2), 0);
                        }
                        break;
                }
            }
        });
    }
}
