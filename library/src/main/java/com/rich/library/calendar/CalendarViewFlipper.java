package com.rich.library.calendar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

public class CalendarViewFlipper  extends ViewFlipper {

    private int musicSize = 2;
    private int mCurrentItem = 0;
    private float originalX;//ACTION_DOWN事件发生时的手指坐标
    private int flipper_width = 0;

    private boolean isMove;
    private float downX = 0f;
    private float downY = 0f;
    private float touchSlop = 10;

    public CalendarViewFlipper(Context context) {
        super(context);
        setLongClickable(true);//设置可以接受事件
        setUpViews();
    }

    public CalendarViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLongClickable(true);//设置可以接受事件
        setUpViews();
    }

    public int getmCurrentItem() {
        return mCurrentItem;
    }


    private void setUpViews() {
        removeAllViews();
        ViewFlipperItemView itemView = new ViewFlipperItemView(getContext());
        itemView.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(itemView, params);

        ViewFlipperItemView itemView1 = new ViewFlipperItemView(getContext());
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(itemView1, params1);
        showPrevious();
    }

    //获取上一个下标
    private int nextItem(int index) {
        mCurrentItem = index;
        if (mCurrentItem >= musicSize) {
            mCurrentItem = 0;
        } else if (mCurrentItem < 0) {
            mCurrentItem = musicSize - 1;
        }
        return mCurrentItem;
    }

    //获取下一个下标
    public int previousItem(int index) {
        mCurrentItem = index;
        if (mCurrentItem >= musicSize) {
            mCurrentItem = 0;
        } else if (mCurrentItem < 0) {
            mCurrentItem = musicSize - 1;
        }
        return mCurrentItem;
    }

    //获取没在屏幕中显示的View
    public View getOtherView() {
        return getChildAt(getChildCount() - 1 - getDisplayedChild());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (flipper_width == 0)
            flipper_width = getWidth();

        Log.e("@@@", "width0 = " + getMeasuredWidth());
        Log.e("@@@", "width1 = " + getMeasuredHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isMove = false;
            originalX = ev.getX();
            downX = ev.getX();
            downY = ev.getY();
        }
        if (isMove) {
            responseOnTouch(ev, ev.getX() - downX > 0);
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }


    public void responseOnTouch(MotionEvent event, boolean isRightScroll) {
        if (isRightScroll && mCurrentItem == 0)
            return;

        if (!isRightScroll && mCurrentItem == 1)
            return;

        float dx = event.getX() - originalX;

        float pageOffset = Math.abs(dx) / flipper_width;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originalX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                getCurrentView().setTranslationX(dx);
                getOtherView().setVisibility(VISIBLE);

                if (dx > 0) {
                    getOtherView().setTranslationX(dx - flipper_width);
                } else {
                    getOtherView().setTranslationX(dx + flipper_width);
                }
                break;
            case MotionEvent.ACTION_UP:
                final boolean isNext = dx < 0;

                if (pageOffset > 0.2) {
                    //切换
                    ValueAnimator animator = ValueAnimator.ofFloat(dx, isNext ? -flipper_width : flipper_width);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getCurrentView().setTranslationX((Float) animation.getAnimatedValue());
                            getOtherView().setTranslationX((Float) animation.getAnimatedValue() + (isNext ? flipper_width : -flipper_width));
                            if (Math.abs((float) animation.getAnimatedValue()) == flipper_width) {
                                Log.e("mCurrentItem", mCurrentItem + "");

                                if (isNext) {
                                    nextItem(mCurrentItem + 1);
                                    showNext();
                                } else {
                                    previousItem(mCurrentItem - 1);
                                    showPrevious();
                                }
                            }
                        }
                    });
                    animator.start();
                } else {
                    //回弹
                    ValueAnimator animator = ValueAnimator.ofFloat(dx, 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getCurrentView().setTranslationX((Float) animation.getAnimatedValue());
                            getOtherView().setTranslationX((Float) animation.getAnimatedValue() + (isNext ? flipper_width : -flipper_width));

                        }
                    });
                    animator.start();
                }
                break;
        }
    }

    public void showNextWithAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, -getWidth());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getCurrentView().setTranslationX((Float) animation.getAnimatedValue());
                getOtherView().setVisibility(VISIBLE);
                getOtherView().setTranslationX((Float) animation.getAnimatedValue() + getWidth());
                if (Math.abs((float) animation.getAnimatedValue()) == getWidth()) {
                    nextItem(mCurrentItem + 1);
                    showNext();
                }
            }
        });
        animator.start();
    }
}
