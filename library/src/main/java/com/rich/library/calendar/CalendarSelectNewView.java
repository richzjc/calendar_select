package com.rich.library.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.rich.library.R;

public class CalendarSelectNewView extends RelativeLayout {

    private CalendarViewFlipper viewFlipper;
    private FrameLayout content;

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


    private void init(Context context){
        viewFlipper = new CalendarViewFlipper(context);
        viewFlipper.setId(R.id.view_flipper);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(241f));
        addView(viewFlipper, params);

        content = new FrameLayout(getContext());
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        contentParams.topMargin = dip2px(220f);
        content.setBackgroundColor(Color.RED);
        addView(content, contentParams);
    }


    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(dpValue * scale + 0.5f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    public void show(){

    }

    public void hide(){

    }
}
