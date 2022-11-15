package com.rich.library.calendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class FlipperItemChildLinearLayout extends LinearLayout {

    public OnClickListener listener;
    private float downRawX;
    private float downRawY;
    private float gap = dip2px(5f);

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(dpValue * scale + 0.5f);
    }


    public FlipperItemChildLinearLayout(Context context) {
        super(context);
    }

    public FlipperItemChildLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipperItemChildLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            downRawX = ev.getRawX();
            downRawY = ev.getRawY();
        }else if(ev.getAction() == MotionEvent.ACTION_UP){
            float rawX = ev.getRawX();
            float rawY = ev.getRawY();
            if(listener != null && Math.abs(downRawX - rawX) < gap && Math.abs(downRawY - rawY) < gap)
                listener.onClick(this);
        }
        return true;
    }
}
