package com.rich.library.calendar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.rich.library.DayTimeEntity;

import java.text.SimpleDateFormat;
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

    private int currentMode = MODE_WEEK;
    private DayTimeEntity selectEntity;
    public Map<String, List<DayTimeEntity>> daytimeMap;

    public DayTimeEntity startTimeEntity;
    public DayTimeEntity endTimeEntity;

    public CalendarViewFlipper(Context context) {
        super(context);
        setLongClickable(true);//设置可以接受事件
        setUpViews();
    }

    public void setCurrentMode(int mode){
        this.currentMode = mode;
        CalendarSelectNewView selectNewView = (CalendarSelectNewView) getParent();
        CalendarTotalView totalView = (CalendarTotalView) selectNewView.getParent();
        totalView.updateMode(currentMode);
    }

    public int getCurrentMode(){
        return currentMode;
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
        boolean isSlideUp = moveY < downY && isMeetSlidingYAngle;
        boolean isSlideDown = moveY > downY && isMeetSlidingYAngle;
        return isSlideUp || isSlideDown;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            getOtherView().setVisibility(View.VISIBLE);
            getCurrentView().setVisibility(View.VISIBLE);
            ((ViewFlipperItemView) getOtherView()).dateLL.setTranslationY(0f);

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
                    updatePreData();
                } else {
                    getOtherView().setTranslationX(dx + flipper_width);
                    updateNextData();
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
                                ((ViewFlipperItemView) getCurrentView()).dateLL.setTranslationY(0);
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

    @Override
    public void showNext() {
        super.showNext();
        ViewFlipperItemView flipper = (ViewFlipperItemView) getCurrentView();
        CalendarSelectNewView selectNewView = (CalendarSelectNewView) getParent();
        selectNewView.calendarLiveData.setValue(flipper.curBindCalendar);
        ViewFlipperItemView itemView = (ViewFlipperItemView) getOtherView();
        itemView.dateLL.setTranslationY(0f);
    }

    @Override
    public void showPrevious() {
        super.showPrevious();
        ViewFlipperItemView flipper = (ViewFlipperItemView) getCurrentView();
        CalendarSelectNewView selectNewView = (CalendarSelectNewView) getParent();
        selectNewView.calendarLiveData.setValue(flipper.curBindCalendar);
        ViewFlipperItemView itemView = (ViewFlipperItemView) getOtherView();
        itemView.dateLL.setTranslationY(0f);
    }

    private void updateNextData() {
        if (currentMode == MODE_WEEK) {
            ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
            DayTimeEntity firstDayTimeEntity = itemView.getFirstShowDayTimeEntity();;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, firstDayTimeEntity.year);
            calendar.set(Calendar.MONTH, firstDayTimeEntity.month);
            calendar.set(Calendar.DAY_OF_MONTH, firstDayTimeEntity.day);
            calendar.add(Calendar.DATE, 7);
            ((ViewFlipperItemView) getOtherView()).bindData(calendar, currentMode, daytimeMap,false);
        } else {
            ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
            Calendar nextCalendar = Calendar.getInstance();
            nextCalendar.setTimeInMillis(itemView.curBindCalendar.getTimeInMillis());
            nextCalendar.add(Calendar.MONTH, 1);
            ((ViewFlipperItemView) getOtherView()).bindData(nextCalendar, currentMode, daytimeMap, false);
        }
    }

    private void updatePreData() {
        if (currentMode == MODE_WEEK) {
            ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
            DayTimeEntity firstDayTimeEntity = itemView.getFirstShowDayTimeEntity();;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, firstDayTimeEntity.year);
            calendar.set(Calendar.MONTH, firstDayTimeEntity.month);
            calendar.set(Calendar.DAY_OF_MONTH, firstDayTimeEntity.day);
            calendar.add(Calendar.DATE, -7);
            ((ViewFlipperItemView) getOtherView()).bindData(calendar, currentMode, daytimeMap,false);
        } else {
            ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
            Calendar preCalendar = Calendar.getInstance();
            preCalendar.setTimeInMillis(itemView.curBindCalendar.getTimeInMillis());
            preCalendar.add(Calendar.MONTH, -1);
            ((ViewFlipperItemView) getOtherView()).bindData(preCalendar, currentMode, daytimeMap, false);
        }
    }

    private boolean checkHasPre() {
        if (currentMode == MODE_MONTH) {
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
        } else if (currentMode == MODE_WEEK) {
            ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
            DayTimeEntity firstDayTimeEntity = itemView.getFirstShowDayTimeEntity();;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, firstDayTimeEntity.year);
            calendar.set(Calendar.MONTH, firstDayTimeEntity.month);
            calendar.set(Calendar.DAY_OF_MONTH, firstDayTimeEntity.day);
            calendar.add(Calendar.DATE, -7);
            int tempYear = calendar.get(Calendar.YEAR);
            int tempMonth = calendar.get(Calendar.MONTH);
            int tempDay = calendar.get(Calendar.DAY_OF_MONTH);

            if ((tempYear < startTimeEntity.year) || (tempYear == startTimeEntity.year && tempMonth < startTimeEntity.month) || (tempYear == startTimeEntity.year && tempMonth == startTimeEntity.month && tempDay < startTimeEntity.day))
                return false;
            else
                return true;
        } else {
            return true;
        }
    }

    private boolean checkHasNext() {
        if (currentMode == MODE_MONTH) {
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
        } else if (currentMode == MODE_WEEK) {
            ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
            DayTimeEntity firstDayTimeEntity = itemView.getFirstShowDayTimeEntity();;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, firstDayTimeEntity.year);
            calendar.set(Calendar.MONTH, firstDayTimeEntity.month);
            calendar.set(Calendar.DAY_OF_MONTH, firstDayTimeEntity.day);
            calendar.add(Calendar.DATE, 7);
            int tempYear = calendar.get(Calendar.YEAR);
            int tempMonth = calendar.get(Calendar.MONTH);
            int tempDay = calendar.get(Calendar.DAY_OF_MONTH);
            if ((tempYear > endTimeEntity.year) || (tempYear == endTimeEntity.year && tempMonth > endTimeEntity.month) || (tempYear == endTimeEntity.year && tempMonth == endTimeEntity.month && tempDay > endTimeEntity.day))
                return false;
            else
                return true;
        } else {
            return true;
        }
    }

    public void setSelectEntity(DayTimeEntity entity) {
        this.selectEntity = entity;
        CalendarSelectNewView selectNewView = (CalendarSelectNewView) getParent();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, entity.year);
        calendar.set(Calendar.MONTH, entity.month);
        calendar.set(Calendar.DAY_OF_MONTH, entity.day);
        selectNewView.calendarLiveData.setValue(calendar);
    }

    public DayTimeEntity getSelectEntity() {
        return selectEntity;
    }

    public void setcalendarRange(Calendar startCalendar, Calendar endCalendar) {
        calStartEndEntity(startCalendar, endCalendar);
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
            currentCalendar.set(Calendar.DATE, day);
            ((ViewFlipperItemView) getCurrentView()).bindData(currentCalendar, currentMode, daytimeMap, false);
            ((ViewFlipperItemView) getOtherView()).bindData(currentCalendar, currentMode, daytimeMap, false);
        } else {
            startCalendar.set(Calendar.DATE, 1);
            ((ViewFlipperItemView) getCurrentView()).bindData(startCalendar, currentMode, daytimeMap, false);
            ((ViewFlipperItemView) getOtherView()).bindData(startCalendar, currentMode, daytimeMap, false);
        }

        ViewFlipperItemView flipper = (ViewFlipperItemView) getCurrentView();
        CalendarSelectNewView selectNewView = (CalendarSelectNewView) getParent();
        selectNewView.calendarLiveData.setValue(flipper.curBindCalendar);
    }


    public void refreshCurrent() {
        if (daytimeMap == null)
            daytimeMap = new HashMap<>();

        ViewFlipperItemView itemView = (ViewFlipperItemView) getCurrentView();
        itemView.curBindCalendar = null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectEntity.year);
        calendar.set(Calendar.MONTH, selectEntity.month);
        calendar.set(Calendar.DAY_OF_MONTH, selectEntity.day);
        itemView.bindData(calendar, MODE_WEEK, daytimeMap, false);

        ((ViewFlipperItemView) getOtherView()).bindData(calendar, currentMode, daytimeMap, false);
        setSelectEntity(selectEntity);
    }


    public void calStartEndEntity(Calendar startCalendar, Calendar endCalendar) {
        Calendar tempStartCalendar = Calendar.getInstance();
        tempStartCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
        tempStartCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int preIndex = CalendarNewUtil.firstStartIndex(tempStartCalendar);
        tempStartCalendar.add(Calendar.DATE, -preIndex);
        startTimeEntity = new DayTimeEntity(tempStartCalendar.get(Calendar.YEAR), tempStartCalendar.get(Calendar.MONTH), tempStartCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);

        tempStartCalendar.setTimeInMillis(endCalendar.getTimeInMillis());
        int totalCount = CalendarNewUtil.getDayCountOfMonth(tempStartCalendar);
        tempStartCalendar.set(Calendar.DAY_OF_MONTH, totalCount);
        int endIndex = CalendarNewUtil.lastEndIndex(tempStartCalendar, totalCount);
        tempStartCalendar.add(Calendar.DATE, endIndex);
        endTimeEntity = new DayTimeEntity(tempStartCalendar.get(Calendar.YEAR), tempStartCalendar.get(Calendar.MONTH), tempStartCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
    }

    public void switchToToday(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setSelectEntity(new DayTimeEntity(year, month, day, 0, 0));
        ViewFlipperItemView itemview = (ViewFlipperItemView) getCurrentView();
        itemview.bindData(calendar, currentMode, daytimeMap, true);
    }
}
