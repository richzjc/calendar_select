package com.rich.library.calendar;

import static com.rich.library.calendar.CalendarViewFlipper.MODE_MONTH;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_SCROLL;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import java.util.Calendar;

public class CalendarSelectNewView extends RelativeLayout {

    private CalendarViewFlipper viewFlipper;
    private FrameLayout content;
    private View handleView;

    private float downX;
    private float downY;

    private int CLICK_VIEW_FLIPPER = 1;
    private int CLICK_FRAMELAYOUT = 2;
    private boolean isVerticleScroll;
    private boolean isFirstMove;
    private float contentTranslateY;
    private float itemTranslateY;
    private boolean isClickViewFlipper;
    private final float SLIDE_ANGLE = 45;

    private int clickViewFlag = -1;
    private AnimatorSet set;
    public MutableLiveData<Calendar> calendarLiveData = new MutableLiveData<>();

    public CalendarSelectNewView(Context context) {
        super(context);
        init(context);
    }

    public CalendarSelectNewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarSelectNewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int flipperHeightSpec = MeasureSpec.makeMeasureSpec(dip2px(400f), MeasureSpec.EXACTLY);
        viewFlipper.measure(widthMeasureSpec, flipperHeightSpec);

        int handleHeightSpec = MeasureSpec.makeMeasureSpec(dip2px(20f), MeasureSpec.EXACTLY);
        handleView.measure(widthMeasureSpec, handleHeightSpec);

        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();

        int contentHeightSpec = MeasureSpec.makeMeasureSpec(heightSize - itemView.getTopHeight(), MeasureSpec.EXACTLY);
        content.measure(widthMeasureSpec, contentHeightSpec);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        viewFlipper.layout(0, 0, viewFlipper.getMeasuredWidth(), viewFlipper.getMeasuredHeight());
        content.layout(0, itemView.getTopHeight(), content.getMeasuredWidth(), itemView.getTopHeight() + content.getMeasuredHeight());
        handleView.layout(0, itemView.getTopHeight(), handleView.getMeasuredWidth(), itemView.getTopHeight() + handleView.getMeasuredHeight());
    }

    public boolean pointInView(float localX, float localY, View view) {
        if (view == content) {
            boolean result = localX >= view.getLeft() && localY >= view.getTop() + content.getTranslationY() + handleView.getMeasuredHeight() && localX < view.getRight() &&
                    localY < view.getBottom() + content.getTranslationY();
            return result;
        } else if (view == viewFlipper) {
            ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
            return localX >= view.getLeft() && localY >= view.getTop() && localX < view.getRight() &&
                    localY < view.getBottom() - (itemView.getMaxTranslateY() - content.getTranslationY());
        } else {
            return false;
        }
    }


    private void init(Context context) {
        viewFlipper = new CalendarViewFlipper(context);
        addView(viewFlipper);

        content = new FrameLayout(getContext());
        content.setBackgroundColor(Color.RED);
        content.setPadding(0, dip2px(20f), 0, 0);
        addView(content);

        handleView = new View(getContext());
        handleView.setBackgroundColor(Color.BLACK);
        addView(handleView);
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(dpValue * scale + 0.5f);
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
        if (viewFlipper.currentMode == MODE_SCROLL) {
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isVerticleScroll = false;
            isFirstMove = true;
            downX = ev.getX();
            downY = ev.getY();
            ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
            contentTranslateY = content.getTranslationY();
            itemTranslateY = itemView.dateLL.getTranslationY();
            int actionIndex = ev.getActionIndex();
            float x = ev.getX(actionIndex);
            float y = ev.getY(actionIndex);
            try {
                boolean isClickFrameLayout = pointInView(x, y, content);
                isClickViewFlipper = pointInView(x, y, viewFlipper);
                if (isClickViewFlipper) {
                    clickViewFlag = CLICK_VIEW_FLIPPER;
                    return viewFlipper.dispatchTouchEvent(ev);
                } else if (isClickFrameLayout && viewFlipper.currentMode == MODE_MONTH) {
                    clickViewFlag = CLICK_FRAMELAYOUT;
                    hide();
                    return true;
                } else if (isClickFrameLayout) {
                    return content.dispatchTouchEvent(ev);
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (isFirstMove) {
                isFirstMove = false;
                isVerticleScroll = checkIsVerticle(ev);
            }

            if (clickViewFlag == CLICK_VIEW_FLIPPER) {
                if (isVerticleScroll) {
                    updateViewPosition(ev);
                    return true;
                } else {
                    return viewFlipper.dispatchTouchEvent(ev);
                }
            } else if (pointInView(ev.getX(), ev.getY(), content)) {
                return content.dispatchTouchEvent(ev);
            } else {
                return false;
            }
        } else {
            if (clickViewFlag == CLICK_VIEW_FLIPPER) {
                if (isVerticleScroll) {
                    float dy = ev.getY() - downY;
                    if (dy > 0) {
                        if (dy >= dip2px(40f))
                            show();
                        else
                            hide(false);
                    } else if (dy < 0) {
                        if (Math.abs(dy) >= dip2px(40f))
                            hide(true);
                        else
                            show();
                    }

                    return true;
                } else
                    return viewFlipper.dispatchTouchEvent(ev);
            } else if (pointInView(ev.getX(), ev.getY(), content)) {
                return content.dispatchTouchEvent(ev);
            } else {
                return true;
            }
        }
    }

    private void updateViewPosition(MotionEvent ev) {
        float px = ev.getY() - downY;
        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        int maxTransY = itemView.getMaxTranslateY();
        int itemTransY = itemView.getFlipperTransLateY();
        float realContentTransY = contentTranslateY + px;
        if (realContentTransY > maxTransY)
            realContentTransY = maxTransY;
        else if (realContentTransY < 0)
            realContentTransY = 0;
        content.setTranslationY(realContentTransY);
        handleView.setTranslationY(realContentTransY);


        float itemPx = (itemTransY * px) / maxTransY;
        float realItemTranslateY = itemTranslateY + itemPx;
        if (realItemTranslateY < -itemTransY)
            realItemTranslateY = -itemTransY;
        else if (realItemTranslateY > 0)
            realItemTranslateY = 0;

        itemView.dateLL.setTranslationY(realItemTranslateY);
    }

    public void show() {
        if (set != null && set.isRunning())
            set.cancel();

        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        int maxTransY = itemView.getMaxTranslateY();

        float contentTranslateY = content.getTranslationY();

        float time = Math.abs(maxTransY - contentTranslateY) * 300f / maxTransY;

        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "translationY", contentTranslateY, maxTransY);
        ObjectAnimator handleAnimator = ObjectAnimator.ofFloat(handleView, "translationY", contentTranslateY, maxTransY);

        float itemTransY = itemView.dateLL.getTranslationY();

        ObjectAnimator flipperAnimator = ObjectAnimator.ofFloat(itemView.dateLL, "translationY", itemTransY, 0);

        set = new AnimatorSet();
        set.playTogether(animator, handleAnimator, flipperAnimator);
        set.setDuration((long) time);
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewFlipper.currentMode = MODE_SCROLL;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (handleView.getTranslationY() != 0)
                    viewFlipper.currentMode = MODE_MONTH;
                else
                    viewFlipper.currentMode = MODE_WEEK;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    private void hide(boolean isNeedReLocation) {
        if (set != null && set.isRunning())
            set.cancel();

        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        int maxTransY = itemView.getMaxTranslateY();

        float contentTranslateY = content.getTranslationY();
        float time = Math.abs(contentTranslateY) * 300f / maxTransY;

        final boolean reLocation = isNeedReLocation;

        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "translationY", contentTranslateY, 0);
        ObjectAnimator handleAnimator = ObjectAnimator.ofFloat(handleView, "translationY", contentTranslateY, 0);

        int itemTransY = itemView.getFlipperTransLateY();
        float itemTranslateY = itemView.dateLL.getTranslationY();
        ObjectAnimator flipperAnimator = ObjectAnimator.ofFloat(itemView.dateLL, "translationY", itemTranslateY, -itemTransY);

        set = new AnimatorSet();
        set.playTogether(animator, handleAnimator, flipperAnimator);
        Log.e("time", "time = " + time);
        set.setDuration((long) time);
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewFlipper.currentMode = MODE_SCROLL;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (handleView.getTranslationY() != 0)
                    viewFlipper.currentMode = MODE_MONTH;
                else
                    viewFlipper.currentMode = MODE_WEEK;

                if (viewFlipper.currentMode == MODE_WEEK && reLocation) {
                    viewFlipper.refreshCurrent();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    public void hide() {
        float contentTranslateY = content.getTranslationY();
        final boolean reLocation = (contentTranslateY > dip2px(40f));
        hide(reLocation);
    }

    public void setCalendarRange(Calendar startCalendar, Calendar endCalendar, int currentModel) {
        viewFlipper.setcalendarRange(startCalendar, endCalendar);
        viewFlipper.currentMode = currentModel;
        if (currentModel == MODE_WEEK) {
            handleView.setTranslationY(0);
            content.setTranslationY(0);
        } else if (currentModel == MODE_MONTH) {
            ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
            int maxY = itemView.getMaxTranslateY();
            content.setTranslationY(maxY);
            handleView.setTranslationY(maxY);
            itemView.dateLL.setTranslationY(0);
        }
    }
}
