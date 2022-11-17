package com.rich.library.calendar;

import static com.rich.library.calendar.CalendarViewFlipper.MODE_MONTH;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_SCROLL;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rich.library.R;

public class CalendarSwitchView extends LinearLayout {

    private int currentMode = MODE_WEEK;
    private TextView weekView;
    private TextView monthView;
    private View bgView;
    private GradientDrawable bgDrawable;
    private GradientDrawable totalBgDrawable;

    public CalendarSwitchView(Context context) {
        super(context);
        init(context);
    }

    public CalendarSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCurrentMode(int mode) {
        if(mode == currentMode && mode != MODE_SCROLL)
            return;
        this.currentMode = mode;
        float currentTranslateX = bgView.getTranslationX();
        float endTranslateX = 0;
        if (mode == MODE_WEEK) {
            endTranslateX = 0;
        } else if (mode == MODE_MONTH) {
            endTranslateX = weekView.getMeasuredWidth();
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(bgView, "translationX", currentTranslateX, endTranslateX);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(100);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(currentMode == MODE_WEEK){
                    weekView.setTextColor(Color.WHITE);
                    monthView.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color2_666666));
                }else{
                    weekView.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color2_666666));
                    monthView.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void init(Context context) {
        weekView = new TextView(context);
        monthView = new TextView(context);
        weekView.setGravity(Gravity.CENTER);
        monthView.setGravity(Gravity.CENTER);

        weekView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        monthView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);

        bgView = new View(getContext());

        weekView.setText("周");
        monthView.setText("月");

        if(currentMode == MODE_WEEK){
            weekView.setTextColor(Color.WHITE);
            monthView.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color2_666666));
        }else{
            weekView.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color2_666666));
            monthView.setTextColor(Color.WHITE);
        }

        addView(bgView);
        addView(weekView);
        addView(monthView);

        weekView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMode != MODE_WEEK){
                    setCurrentMode(MODE_WEEK);
                    CalendarHeaderView headerView = (CalendarHeaderView) getParent();
                    CalendarTotalView totalView = (CalendarTotalView) headerView.getParent();
                    totalView.selectNewView.hide();
                }
            }
        });

        monthView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentMode != MODE_MONTH){
                    setCurrentMode(MODE_MONTH);
                    CalendarHeaderView headerView = (CalendarHeaderView) getParent();
                    CalendarTotalView totalView = (CalendarTotalView) headerView.getParent();
                    totalView.selectNewView.show();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthSpec = MeasureSpec.makeMeasureSpec(widthSize / 2, MeasureSpec.EXACTLY);
        int heightSepc = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        weekView.measure(widthSpec, heightSepc);
        monthView.measure(widthSpec, heightSepc);
        bgView.measure(widthSpec, heightSepc);

        if (bgDrawable == null) {
            bgDrawable = new GradientDrawable();
            bgDrawable.setColor(getContext().getResources().getColor(R.color.day_mode_theme_color_1478f0));
            bgDrawable.setCornerRadius(Util.dip2px(heightSize / 2, getContext()));
            bgView.setBackground(bgDrawable);
        }

        if (totalBgDrawable == null) {
            totalBgDrawable = new GradientDrawable();
            totalBgDrawable.setColor(getContext().getResources().getColor(R.color.day_mode_background_color_dddddd));
            totalBgDrawable.setCornerRadius(Util.dip2px(heightSize / 2, getContext()));
            setBackground(totalBgDrawable);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        bgView.layout(0, 0, bgView.getMeasuredWidth(), bgView.getMeasuredHeight());
        weekView.layout(0, 0, weekView.getMeasuredWidth(), weekView.getMeasuredHeight());
        monthView.layout(weekView.getMeasuredWidth(), 0, getMeasuredWidth(), monthView.getMeasuredHeight());
        if (currentMode == MODE_WEEK)
            bgView.setTranslationX(0);
        else if (currentMode == MODE_MONTH)
            bgView.setTranslationX(weekView.getMeasuredWidth());
    }
}
