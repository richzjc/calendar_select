package com.rich.library.calendar;

import static com.rich.library.calendar.CalendarViewFlipper.MODE_MONTH;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

    public LinearLayout dateLL;
    public LinearLayout weekLL;

    private int selectWeekNumOfMonth;
    private View sixView;
    private View fiveView;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
    //TODO 看看这个地方是否可以优化的， 是不是每次进来都是需要刷新数据的
    public Calendar curBindCalendar;

    private final OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

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

    public int getTopHeight() {
        return weekLL.getMeasuredHeight() + firstLL.getMeasuredHeight();
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

        dateLL = findViewById(R.id.date_ll);
        weekLL = findViewById(R.id.week_LL);

        sixView = findViewById(R.id.six_view);
        fiveView = findViewById(R.id.five_view);

        addOnclickListener(firstLL);
        addOnclickListener(secondLL);
        addOnclickListener(thirdLL);
        addOnclickListener(forthLL);
        addOnclickListener(fiveLL);
        addOnclickListener(sixLL);
    }

    private void addOnclickListener(LinearLayout ll) {
        int childCount = ll.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ll.getChildAt(i).setOnClickListener(itemClickListener);
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(dpValue * scale + 0.5f);
    }


    public int getMaxTranslateY() {
        return getHeight() - secondLL.getTop() - dip2px(20f) - weekLL.getHeight();
    }

    public int getFlipperTransLateY() {
        if (selectWeekNumOfMonth == 1) {
            return 0;
        } else if (selectWeekNumOfMonth == 2) {
            return secondLL.getTop();
        } else if (selectWeekNumOfMonth == 3) {
            return thirdLL.getTop();
        } else if (selectWeekNumOfMonth == 4) {
            return forthLL.getTop();
        } else if (selectWeekNumOfMonth == 5) {
            return fiveLL.getTop();
        } else if (selectWeekNumOfMonth == 6) {
            return sixLL.getTop();
        } else {
            return 0;
        }
    }


    public void bindData(Calendar calendar, int mode, int selectWeekNumOfMonth, Map<String, List<DayTimeEntity>> map) {
        this.selectWeekNumOfMonth = selectWeekNumOfMonth;
        boolean flag;
        if (curBindCalendar == null)
            flag = true;
        else {
            int bindYear = curBindCalendar.get(Calendar.YEAR);
            int bindMonth = curBindCalendar.get(Calendar.MONTH);
            int curYear = calendar.get(Calendar.YEAR);
            int curMonth = calendar.get(Calendar.MONTH);
            if (bindYear == curYear && curMonth == bindMonth)
                flag = false;
            else
                flag = true;
        }
        if (flag) {
            CalendarNewUtil.initAllDayTimeEntity(map, calendar);
            curBindCalendar = calendar;

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

            DayTimeEntity first = list.get(0);
            DayTimeEntity last = list.get(listSize - 1);

            Calendar lastCalendar = Calendar.getInstance();
            lastCalendar.set(Calendar.YEAR, last.year);
            lastCalendar.set(Calendar.MONTH, last.month);
            lastCalendar.set(Calendar.DAY_OF_MONTH, last.day);
            int lastEndIndex = CalendarNewUtil.lastEndIndex(newCalendar, CalendarNewUtil.getDayCountOfMonth(newCalendar));
            if (weekCount == 5) {
                for (int i = lastEndIndex; i > 0; i--) {
                    lastCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    TextView tv = ((TextView) fiveLL.getChildAt(7 - i));
                    tv.setText(String.valueOf(lastCalendar.get(Calendar.DAY_OF_MONTH)));
                    DayTimeEntity entity = new DayTimeEntity(lastCalendar.get(Calendar.YEAR), lastCalendar.get(Calendar.MONTH), lastCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                    tv.setTag(entity);
                    tv.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color3_999999));
                }
            } else if (weekCount == 6) {
                for (int i = lastEndIndex; i > 0; i--) {
                    lastCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    TextView tv = ((TextView) sixLL.getChildAt(7 - i));
                    tv.setText(String.valueOf(lastCalendar.get(Calendar.DAY_OF_MONTH)));
                    DayTimeEntity entity = new DayTimeEntity(lastCalendar.get(Calendar.YEAR), lastCalendar.get(Calendar.MONTH), lastCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                    tv.setTag(entity);
                    tv.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color3_999999));
                }
            }


            Calendar firstCalendar = Calendar.getInstance();
            firstCalendar.set(Calendar.YEAR, first.year);
            firstCalendar.set(Calendar.MONTH, first.month);
            firstCalendar.set(Calendar.DAY_OF_MONTH, first.day);

            for (int i = 0; i < firstStartIndex; i++) {
                firstCalendar.add(Calendar.DAY_OF_MONTH, -1);
                TextView tv = ((TextView) firstLL.getChildAt(firstStartIndex - 1 - i));
                tv.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color3_999999));
                DayTimeEntity entity = new DayTimeEntity(firstCalendar.get(Calendar.YEAR), firstCalendar.get(Calendar.MONTH), firstCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                tv.setTag(entity);
                tv.setText(String.valueOf(firstCalendar.get(Calendar.DAY_OF_MONTH)));
            }


            for (int i = firstStartIndex; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = ((TextView) firstLL.getChildAt(i));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    tv.setTag(list.get(totalCount));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = ((TextView) secondLL.getChildAt(i));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    tv.setTag(list.get(totalCount));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = ((TextView) thirdLL.getChildAt(i));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    tv.setTag(list.get(totalCount));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = ((TextView) forthLL.getChildAt(i));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    tv.setTag(list.get(totalCount));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = ((TextView) fiveLL.getChildAt(i));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    tv.setTag(list.get(totalCount));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = ((TextView) sixLL.getChildAt(i));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    tv.setTag(list.get(totalCount));
                    totalCount++;
                }
            }

            if (mode == MODE_MONTH) {

            } else if (mode == MODE_WEEK) {

            }
        }
    }
}
