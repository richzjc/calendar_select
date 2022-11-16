package com.rich.library.calendar;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Calendar;

public class CalendarTotalView extends LinearLayout {

    public CalendarHeaderView headerView;
    public CalendarSelectNewView selectNewView;

    public CalendarTotalView(Context context) {
        super(context);
        init(context);
    }

    public CalendarTotalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarTotalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context){
        setOrientation(LinearLayout.VERTICAL);
        headerView = new CalendarHeaderView(getContext());
        selectNewView = new CalendarSelectNewView(getContext());
        selectNewView.calendarLiveData.observeForever(new Observer<Calendar>() {
            @Override
            public void onChanged(@Nullable Calendar calendar) {
                headerView.setLeftTitle(calendar);
            }
        });
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(headerView, params);

        LayoutParams selectParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(selectNewView, selectParams);
    }

    public void setCalendarRange(Calendar startCalendar, Calendar endCalendar, int currentModel) {
        selectNewView.setCalendarRange(startCalendar, endCalendar, currentModel);
    }
}
