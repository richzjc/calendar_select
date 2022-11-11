package com.rich.library.calendar;

import static com.rich.library.calendar.CalendarViewFlipper.MODE_MONTH;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_SCROLL;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.rich.library.DayTimeEntity;
import com.rich.library.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        try {
//            ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
//            int contentHeightSpec = MeasureSpec.makeMeasureSpec(50000, MeasureSpec.EXACTLY);
//            content.measure(widthMeasureSpec, contentHeightSpec);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public boolean pointInView(float localX, float localY, View view) {
        if(view == content){
            return localX >= view.getLeft() && localY >= view.getTop() && localX < view.getRight() &&
                    localY < view.getBottom();
        }else if(view == viewFlipper){
            return localX >= view.getLeft() && localY >= view.getTop() && localX < view.getRight() &&
                    localY < view.getBottom();
        }else{
            return false;
        }
    }


    private void init(Context context) {
        viewFlipper = new CalendarViewFlipper(context);
        viewFlipper.setId(R.id.view_flipper);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(240f));
        addView(viewFlipper, params);

        content = new FrameLayout(getContext());
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        content.setBackgroundColor(Color.RED);
        content.setPadding(0, dip2px(20f), 0, 0);
        addView(content, contentParams);

        handleView = new View(getContext());
        handleView.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams handleParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(20f));
        addView(handleView, handleParams);
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
        if (viewFlipper.currentMode == MODE_SCROLL)
            return true;

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
                } else {
                    return super.dispatchTouchEvent(ev);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            } else {
                return super.dispatchTouchEvent(ev);
            }
        } else {
            if (clickViewFlag == CLICK_VIEW_FLIPPER) {
                if (isVerticleScroll) {
                    float dy = ev.getY() - downY;
                    if (dy > 0) {
                        if (dy >= dip2px(20f))
                            show();
                        else
                            hide();
                    } else if (dy < 0) {
                        if (Math.abs(dy) >= dip2px(20f))
                            hide();
                        else
                            show();
                    }

                    return true;
                } else
                    return viewFlipper.dispatchTouchEvent(ev);
            } else {
                return content.dispatchTouchEvent(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateViewPosition(MotionEvent ev) {
        float px = ev.getY() - downY;
        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        int maxTransY = itemView.getMaxTranslateY();
        int itemTransY = itemView.getFlipperTransLateY();
        float realContentTransY = contentTranslateY + px;
        if (realContentTransY < -maxTransY)
            realContentTransY = -maxTransY;
        else if (realContentTransY > 0)
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
        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        int maxTransY = itemView.getMaxTranslateY();

        float contentTranslateY = content.getTranslationY();

        float time = Math.abs(contentTranslateY) * 200 / maxTransY;

        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "translationY", contentTranslateY, 0);
        ObjectAnimator handleAnimator = ObjectAnimator.ofFloat(handleView, "translationY", contentTranslateY, 0);

        float itemTransY = itemView.dateLL.getTranslationY();

        ObjectAnimator flipperAnimator = ObjectAnimator.ofFloat(itemView.dateLL, "translationY", itemTransY, 0);

        AnimatorSet set = new AnimatorSet();
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
                    viewFlipper.currentMode = MODE_WEEK;
                else
                    viewFlipper.currentMode = MODE_MONTH;
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
        ViewFlipperItemView itemView = (ViewFlipperItemView) viewFlipper.getCurrentView();
        int maxTransY = itemView.getMaxTranslateY();

        float contentTranslateY = content.getTranslationY();
        float time = Math.abs(contentTranslateY - maxTransY) * 200 / maxTransY;

        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "translationY", contentTranslateY, -maxTransY);
        ObjectAnimator handleAnimator = ObjectAnimator.ofFloat(handleView, "translationY", contentTranslateY, -maxTransY);

        int itemTransY = itemView.getFlipperTransLateY();
        float itemTranslateY = itemView.dateLL.getTranslationY();
        ObjectAnimator flipperAnimator = ObjectAnimator.ofFloat(itemView.dateLL, "translationY", itemTranslateY, -itemTransY);

        AnimatorSet set = new AnimatorSet();
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
                    viewFlipper.currentMode = MODE_WEEK;
                else
                    viewFlipper.currentMode = MODE_MONTH;
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

    public void setCalendarRange(Calendar startCalendar, Calendar endCalendar) {
        viewFlipper.setcalendarRange(startCalendar, endCalendar);
    }
}
