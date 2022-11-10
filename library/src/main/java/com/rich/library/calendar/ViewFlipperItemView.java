package com.rich.library.calendar;

import static com.rich.library.calendar.CalendarViewFlipper.MODE_MONTH;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rich.library.DayTimeEntity;
import com.rich.library.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ViewFlipperItemView extends FrameLayout {

    private LinearLayout sixLL;
    private LinearLayout fiveLL;
    private LinearLayout forthLL;
    private LinearLayout thirdLL;
    private LinearLayout secondLL;
    private LinearLayout firstLL;

    private View sixView;
    private View fiveView;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");

    public ViewFlipperItemView(Context context) {
        super(context);
        init(context);
    }

    public ViewFlipperItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewFlipperItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.global_view_calendar_flipper_item, this, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, params);

        sixLL = findViewById(R.id.six_ll);
        fiveLL = findViewById(R.id.five_ll);
        forthLL = findViewById(R.id.forth_ll);
        thirdLL = findViewById(R.id.third_ll);
        secondLL = findViewById(R.id.second_ll);
        firstLL = findViewById(R.id.first_ll);

        sixView = findViewById(R.id.six_view);
        fiveView = findViewById(R.id.five_view);
    }


    public void bindData(Calendar calendar, int mode, int selectWeekNumOfMonth, Map<String, List<DayTimeEntity>> map) {
        CalendarNewUtil.initAllDayTimeEntity(map, calendar);

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(calendar.getTimeInMillis());
        int weekCount = CalendarNewUtil.getWeekCountOfMonth(newCalendar);

        if (weekCount == 5) {
            fiveView.setVisibility(View.VISIBLE);
            fiveLL.setVisibility(View.VISIBLE);
            sixView.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
        } else if (weekCount == 6) {
            fiveView.setVisibility(View.VISIBLE);
            fiveLL.setVisibility(View.VISIBLE);
            sixView.setVisibility(View.VISIBLE);
            sixLL.setVisibility(View.VISIBLE);
        } else {
            fiveView.setVisibility(View.GONE);
            fiveLL.setVisibility(View.GONE);
            sixView.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
        }


        String key = format.format(calendar.getTime());
        List<DayTimeEntity> list = map.get(key);
        int listSize = list.size();
        int firstStartIndex = CalendarNewUtil.firstStartIndex(newCalendar);
        int totalCount = 0;

        for (int i = firstStartIndex; i < 7; i++) {
            if (totalCount < listSize) {
                ((TextView) firstLL.getChildAt(i)).setText(String.valueOf(list.get(totalCount).day));
                totalCount ++;
            }
        }

        for (int i = 0; i < 7; i++) {
            if (totalCount < listSize) {
                ((TextView) secondLL.getChildAt(i)).setText(String.valueOf(list.get(totalCount).day));
                totalCount ++;
            }
        }

        for (int i = 0; i < 7; i++) {
            if (totalCount < listSize) {
                ((TextView) thirdLL.getChildAt(i)).setText(String.valueOf(list.get(totalCount).day));
                totalCount ++;
            }
        }

        for (int i = 0; i < 7; i++) {
            if (totalCount < listSize) {
                ((TextView) forthLL.getChildAt(i)).setText(String.valueOf(list.get(totalCount).day));
                totalCount ++;
            }
        }

        for (int i = 0; i < 7; i++) {
            if (totalCount < listSize) {
                ((TextView) fiveLL.getChildAt(i)).setText(String.valueOf(list.get(totalCount).day));
                totalCount ++;
            }
        }

        for (int i = 0; i < 7; i++) {
            if (totalCount < listSize) {
                ((TextView) sixLL.getChildAt(i)).setText(String.valueOf(list.get(totalCount).day));
                totalCount ++;
            }
        }

        if (mode == MODE_MONTH) {

        } else if (mode == MODE_WEEK) {

        }
    }
}
