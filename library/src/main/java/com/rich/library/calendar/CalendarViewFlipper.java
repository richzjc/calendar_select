package com.rich.library.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

public class CalendarViewFlipper  extends ViewFlipper {

    public CalendarViewFlipper(Context context) {
        super(context);
    }

    public CalendarViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
