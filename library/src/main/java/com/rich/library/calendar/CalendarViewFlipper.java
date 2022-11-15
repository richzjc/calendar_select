package com.rich.library.calendar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.rich.library.DayTimeEntity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarViewFlipper extends ViewFlipper {

    private int musicSize = 2;
    private int mCurrentItem = 0;
    private float originalX; //ACTION_DOWN事件发生时的手指坐标
    private int flipper_width = 0;

    private float downX = 0f;
    private float downY = 0f;
    private final float SLIDE_ANGLE = 45;
    
    private boolean isVerticleScroll;
    private boolean isFirstMove;
    private int duration = 200;

    public static final int MODE_WEEK = 1;
    public static final int MODE_SCROLL = 3;
    public static final int MODE_MONTH = 2;
    private Calendar startCalendar;
    private Calendar endCalendar;

    public int currentMode = MODE_WEEK;
    private DayTimeEntity selectEntity;
    public int weekOffsetCount = 0;
    private Map<String, List<DayTimeEntity>> daytimeMap;

    private Calendar curCalendar;
    private Calendar preCalendar;
    private Calendar nextCalendar;

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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(itemView, params);

        ViewFlipperItemView itemView1 = new ViewFlipperItemView(getContext());
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        itemView1.setVisibility(View.INVISIBLE);
        itemView1.setTranslationX(Util.getScreenWidth(getContext()));
        addView(itemView1, params1);
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
    }


    private boolean checkIsVerticle(MotionEvent ev) {
        float moveX = ev.getX();
        float moveY = ev.getY();
        float xDiff = Math.abs(moveX - downX);
        float yDiff = Math.abs(moveY - downY);
        double squareRoot = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        //滑动的角度
        int yAngle = Math.round((float) (Math.asin(yDiff / squareRoot) / Math.PI * 180));
        int xAngle = Math.round((float) (Math.asin(xDiff / squareRoot) / Math.PI * 180));
        boolean isMeetSlidingYAngle = yAngle > SLIDE_ANGLE;//滑动角度是否大于45du
        boolean isMeetSlidingXAngle = xAngle > SLIDE_ANGLE;//滑动角度是否大于45du
        boolean isSlideUp = moveY < downY && isMeetSlidingYAngle;
        boolean isSlideDown = moveY > downY && isMeetSlidingYAngle;
        return isSlideUp || isSlideDown;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            getOtherView().setVisibility(View.VISIBLE);
            getCurrentView().setVisibility(View.VISIBLE);
            originalX = ev.getX();
            downX = ev.getX();
            downY = ev.getY();
            isVerticleScroll = false;
            isFirstMove = true;
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (isFirstMove) {
                isFirstMove = false;
                isVerticleScroll = checkIsVerticle(ev);
            }
        }

        if (!isVerticleScroll) {
            if (ev.getX() - downX > 0 && checkHasPre())
                responseOnTouch(ev);
            else if (ev.getX() - downX < 0 && checkHasNext()) {
                responseOnTouch(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public void responseOnTouch(MotionEvent event) {
        float dx = event.getX() - originalX;

        float pageOffset = Math.abs(dx) / flipper_width;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originalX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                getCurrentView().setTranslationX(dx);
                if (dx > 0) {
                    getOtherView().setTranslationX(dx - flipper_width);
                    ((ViewFlipperItemView) getOtherView()).bindData(preCalendar, currentMode, daytimeMap);
                } else {
                    getOtherView().setTranslationX(dx + flipper_width);
                    ((ViewFlipperItemView) getOtherView()).bindData(nextCalendar, currentMode, daytimeMap);
                }
                break;
            case MotionEvent.ACTION_UP:
                final boolean isNext = dx < 0;

                if (pageOffset > 0.1) {
                    //切换
                    ValueAnimator animator = ValueAnimator.ofFloat(dx, isNext ? -flipper_width : flipper_width);
                    animator.setDuration(duration);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            getCurrentView().setTranslationX((Float) animation.getAnimatedValue());
                            getOtherView().setTranslationX((Float) animation.getAnimatedValue() + (isNext ? flipper_width : -flipper_width));
                            if (Math.abs((float) animation.getAnimatedValue()) == flipper_width) {
                                ((ViewFlipperItemView)getCurrentView()).dateLL.setTranslationY(0);
                                if (isNext) {
                                    nextItem(mCurrentItem + 1);
                                    showNext();
                                } else {
                                    previousItem(mCurrentItem - 1);
                                    showPrevious();
                                }

                                initCurCalendar(((ViewFlipperItemView) getCurrentView()).curBindCalendar);
                            }
                        }
                    });
                    animator.start();
                } else {
                    //回弹
                    ValueAnimator animator = ValueAnimator.ofFloat(dx, 0);
                    animator.setDuration(duration);
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


    private void initCurCalendar(Calendar curCalendar) {
        this.curCalendar = curCalendar;

        Calendar preCalendar = Calendar.getInstance();
        preCalendar.setTimeInMillis(curCalendar.getTimeInMillis());
        preCalendar.add(Calendar.MONTH, -1);
        this.preCalendar = preCalendar;

        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.setTimeInMillis(curCalendar.getTimeInMillis());
        nextCalendar.add(Calendar.MONTH, 1);
        this.nextCalendar = nextCalendar;
    }

    private boolean checkHasPre() {
        //TODO 需要区分是月还是周
        Calendar currentCalendar = ((ViewFlipperItemView) getCurrentView()).curBindCalendar;
        if (currentCalendar == null || startCalendar == null || endCalendar == null)
            return false;

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTimeInMillis(currentCalendar.getTimeInMillis());
        tempCalendar.add(Calendar.MONTH, -1);
        int tempYear = tempCalendar.get(Calendar.YEAR);
        int startYear = startCalendar.get(Calendar.YEAR);

        int tempMonth = tempCalendar.get(Calendar.MONTH);
        int startMonth = startCalendar.get(Calendar.MONTH);

        if ((tempYear < startYear) || (tempYear == startYear && tempMonth < startMonth))
            return false;
        else
            return true;
    }


    private boolean checkHasNext() {
        //TODO 需要区分是月还是周
        Calendar currentCalendar = ((ViewFlipperItemView) getCurrentView()).curBindCalendar;
        if (currentCalendar == null || startCalendar == null || endCalendar == null)
            return false;

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTimeInMillis(currentCalendar.getTimeInMillis());
        tempCalendar.add(Calendar.MONTH, +1);
        int tempYear = tempCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);

        int tempMonth = tempCalendar.get(Calendar.MONTH);
        int endMonth = endCalendar.get(Calendar.MONTH);

        if ((tempYear > endYear) || (tempYear == endYear && tempMonth > endMonth))
            return false;
        else
            return true;
    }

    public void setSelectEntity(DayTimeEntity entity){
        this.selectEntity = entity;
    }

    public DayTimeEntity getSelectEntity(){
        return selectEntity;
    }

    public void setcalendarRange(Calendar startCalendar, Calendar endCalendar) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        setSelectEntity(new DayTimeEntity(year, month, day, 0, 0));
        if (daytimeMap == null)
            daytimeMap = new HashMap<>();

        this.startCalendar = startCalendar;
        this.endCalendar = endCalendar;

        Calendar currentCalendar = Calendar.getInstance();

        long startTime = startCalendar.getTimeInMillis();
        long endTime = endCalendar.getTimeInMillis();
        long curTime = currentCalendar.getTimeInMillis();

        if (curTime >= startTime && curTime <= endTime) {
            ((ViewFlipperItemView) getCurrentView()).bindData(currentCalendar, currentMode, daytimeMap);
            initCurCalendar(currentCalendar);
            ((ViewFlipperItemView) getOtherView()).bindData(nextCalendar, currentMode, daytimeMap);
        } else {
            ((ViewFlipperItemView) getCurrentView()).bindData(startCalendar, currentMode, daytimeMap);
            initCurCalendar(startCalendar);
            ((ViewFlipperItemView) getOtherView()).bindData(nextCalendar, currentMode, daytimeMap);
        }
    }
}
