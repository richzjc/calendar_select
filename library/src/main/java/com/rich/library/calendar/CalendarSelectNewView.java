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
    private int CLICK_HANDLE_VIEW = 2;
    private int CLICK_FRAMELAYOUT = 3;

    private boolean eventResult = false;

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

    public boolean pointInView(float localX, float localY, View view) {
        return localX >= view.getLeft() && localY >= view.getTop() && localX < view.getRight() &&
                localY < view.getBottom();
    }


    private void init(Context context) {
        viewFlipper = new CalendarViewFlipper(context);
        viewFlipper.setId(R.id.view_flipper);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(240f));
        addView(viewFlipper, params);

        content = new FrameLayout(getContext());
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        contentParams.topMargin = dip2px(220f);
        content.setBackgroundColor(Color.RED);
        content.setPadding(0, dip2px(20f), 0, 0);
        addView(content, contentParams);

        handleView = new View(getContext());
        handleView.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams handleParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(20f));
        handleParams.topMargin = dip2px(220f);
        addView(handleView, handleParams);
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(dpValue * scale + 0.5f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = ev.getX();
            downY = ev.getY();
            responseActionDown(ev);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {

        }

        return super.dispatchTouchEvent(ev);
    }

    private void responseActionDown(MotionEvent ev) {
        int actionIndex = ev.getActionIndex();
        float x = ev.getX(actionIndex);
        float y = ev.getY(actionIndex);
        try {

            boolean isClickHandleView = pointInView(x, y, handleView);
            boolean isClickFrameLayout = pointInView(x, y, content);
            boolean isClickViewFlipper = pointInView(x, y, viewFlipper);
            if (isClickFrameLayout && viewFlipper.currentMode == MODE_MONTH) {
                eventResult = true;
                hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void show() {

    }

    public void hide() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "translationY", 0, -dip2px(240f));
        ObjectAnimator handleAnimator = ObjectAnimator.ofFloat(handleView, "translationY", 0, -dip2px(240f));


        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, handleAnimator);
        set.setDuration(300);
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
