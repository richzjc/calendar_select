package com.rich.library.calendar;

import static com.rich.library.calendar.CalendarNewUtil.getNumSelectWeekOfMonth;
import static com.rich.library.calendar.CalendarNewUtil.getWeekCountOfMonth;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_MONTH;
import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.content.Context;
import android.content.res.Resources;
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

    private DayTimeEntity firstDayTimeEntity;
    private DayTimeEntity lastDayTimeEntity;

    private View sixView;
    private View fiveView;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
    public Calendar curBindCalendar;
    private boolean isFirstInitFlag = true;

    private final OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
            DayTimeEntity clickEntity = (DayTimeEntity) v.getTag();
            flipper.setSelectEntity(clickEntity);
            invalidateSelectBg();
            ((ViewFlipperItemView) flipper.getOtherView()).invalidateSelectBg();
            CalendarSelectNewView newView = (CalendarSelectNewView) flipper.getParent();
            newView.hide();

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
        params.bottomMargin = dip2px(25f);
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
            FlipperItemChildLinearLayout itemChildLinearLayout = (FlipperItemChildLinearLayout) ll.getChildAt(i);
            itemChildLinearLayout.listener = itemClickListener;
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) Math.ceil(dpValue * scale + 0.5f);
    }


    public int getMaxTranslateY() {
        return getHeight() - getTopHeight() - dip2px(20f);
    }

    public int getSelectWeekNumofMonth() {
        CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
        if (curBindCalendar == null || flipper.getSelectEntity() == null)
            return 0;
        int year = curBindCalendar.get(Calendar.YEAR);
        int month = curBindCalendar.get(Calendar.MONTH);
        if (year == flipper.getSelectEntity().year && month == flipper.getSelectEntity().month)
            return getNumSelectWeekOfMonth(flipper.getSelectEntity().year, flipper.getSelectEntity().month, flipper.getSelectEntity().day);

        if (firstDayTimeEntity != null && flipper.getSelectEntity().year == firstDayTimeEntity.year && flipper.getSelectEntity().month == firstDayTimeEntity.month && flipper.getSelectEntity().day >= firstDayTimeEntity.day)
            return 1;

        if (lastDayTimeEntity != null && flipper.getSelectEntity().year == lastDayTimeEntity.year && flipper.getSelectEntity().month == lastDayTimeEntity.month && flipper.getSelectEntity().day <= lastDayTimeEntity.day)
            return getWeekCountOfMonth(curBindCalendar);

        return 0;
    }

    public int getFlipperTransLateY() {
        CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
        if (curBindCalendar != null && flipper.getSelectEntity() != null) {
            int selectWeekNumOfMonth = getSelectWeekNumofMonth();
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
        return 0;
    }

    public TextView getTextView(LinearLayout ll, int index, DayTimeEntity entity) {
        ViewGroup viewGroup = (ViewGroup) ll.getChildAt(index);
        viewGroup.setTag(entity);
        TextView tv = (TextView) viewGroup.getChildAt(0);
        CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
        try {
            if (flipper.getSelectEntity() == null || entity == null) {
                tv.setBackground(null);
            } else if (flipper.getSelectEntity().year == entity.year && flipper.getSelectEntity().month == entity.month && flipper.getSelectEntity().day == entity.day) {
                tv.setBackground(getContext().getResources().getDrawable(R.drawable.global_drawable_circle_select));
            } else {
                tv.setBackground(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tv;
    }

    public View dotView(LinearLayout ll, int index) {
        ViewGroup viewGroup = (ViewGroup) ll.getChildAt(index);
        return viewGroup.getChildAt(1);
    }

    public void invalidateSelectBg() {
        for (int i = 0; i < 7; i++) {
            getTextView(firstLL, i, (DayTimeEntity) firstLL.getChildAt(i).getTag());
        }

        for (int i = 0; i < 7; i++) {
            getTextView(secondLL, i, (DayTimeEntity) secondLL.getChildAt(i).getTag());
        }

        for (int i = 0; i < 7; i++) {
            getTextView(thirdLL, i, (DayTimeEntity) thirdLL.getChildAt(i).getTag());
        }

        for (int i = 0; i < 7; i++) {
            getTextView(forthLL, i, (DayTimeEntity) forthLL.getChildAt(i).getTag());
        }

        if (fiveLL.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < 7; i++) {
                getTextView(fiveLL, i, (DayTimeEntity) fiveLL.getChildAt(i).getTag());
            }
        }

        if (sixLL.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < 7; i++) {
                getTextView(sixLL, i, (DayTimeEntity) sixLL.getChildAt(i).getTag());
            }
        }
    }

    public void bindData(Calendar calendar, int mode, Map<String, List<DayTimeEntity>> map) {
        CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
        CalendarSelectNewView selectNewView = (CalendarSelectNewView) flipper.getParent();
        if(flipper.getCurrentView() == this){
            selectNewView.calendarLiveData.setValue(calendar);
        }

        if (curBindCalendar != null)
            isFirstInitFlag = false;
        else
            isFirstInitFlag = true;

        boolean flag;
        if (curBindCalendar == null)
            flag = true;
        else if (mode == MODE_WEEK) {
            int bindYear = curBindCalendar.get(Calendar.YEAR);
            int bindMonth = curBindCalendar.get(Calendar.MONTH);
            int bindDay = curBindCalendar.get(Calendar.DAY_OF_MONTH);

            int curYear = calendar.get(Calendar.YEAR);
            int curMonth = calendar.get(Calendar.MONTH);
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (bindYear == curYear && curMonth == bindMonth && bindDay == curDay)
                flag = false;
            else
                flag = true;
        } else {
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
            curBindCalendar = calendar;
            CalendarNewUtil.initAllDayTimeEntity(map, calendar);
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTimeInMillis(calendar.getTimeInMillis());
            int weekCount = getWeekCountOfMonth(newCalendar);

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
                    DayTimeEntity entity = new DayTimeEntity(lastCalendar.get(Calendar.YEAR), lastCalendar.get(Calendar.MONTH), lastCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                    TextView tv = getTextView(fiveLL, 7 - i, entity);
                    tv.setText(String.valueOf(lastCalendar.get(Calendar.DAY_OF_MONTH)));
                    tv.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color3_999999));

                    if (i == 1) {
                        lastDayTimeEntity = entity;
                    }
                }
            } else if (weekCount == 6) {
                for (int i = lastEndIndex; i > 0; i--) {
                    lastCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    DayTimeEntity entity = new DayTimeEntity(lastCalendar.get(Calendar.YEAR), lastCalendar.get(Calendar.MONTH), lastCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                    TextView tv = getTextView(sixLL, 7 - i, entity);
                    tv.setText(String.valueOf(lastCalendar.get(Calendar.DAY_OF_MONTH)));
                    tv.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color3_999999));
                    if (i == 1) {
                        lastDayTimeEntity = entity;
                    }
                }
            }


            Calendar firstCalendar = Calendar.getInstance();
            firstCalendar.set(Calendar.YEAR, first.year);
            firstCalendar.set(Calendar.MONTH, first.month);
            firstCalendar.set(Calendar.DAY_OF_MONTH, first.day);

            for (int i = 0; i < firstStartIndex; i++) {
                firstCalendar.add(Calendar.DAY_OF_MONTH, -1);
                DayTimeEntity entity = new DayTimeEntity(firstCalendar.get(Calendar.YEAR), firstCalendar.get(Calendar.MONTH), firstCalendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                TextView tv = getTextView(firstLL, firstStartIndex - 1 - i, entity);
                tv.setTextColor(getContext().getResources().getColor(R.color.day_mode_text_color3_999999));
                tv.setText(String.valueOf(firstCalendar.get(Calendar.DAY_OF_MONTH)));
                if (i == firstStartIndex - 1) {
                    firstDayTimeEntity = entity;
                }
            }

            for (int i = firstStartIndex; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = getTextView(firstLL, i, list.get(totalCount));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = getTextView(secondLL, i, list.get(totalCount));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = getTextView(thirdLL, i, list.get(totalCount));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = getTextView(forthLL, i, list.get(totalCount));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = getTextView(fiveLL, i, list.get(totalCount));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    totalCount++;
                }
            }

            for (int i = 0; i < 7; i++) {
                if (totalCount < listSize) {
                    TextView tv = getTextView(sixLL, i, list.get(totalCount));
                    tv.setText(String.valueOf(list.get(totalCount).day));
                    totalCount++;
                }
            }

            if (mode == MODE_MONTH) {

            } else if (mode == MODE_WEEK) {

            }
        }
    }

    //TODO 下面这个方法有问题
    private int getWeekTranslateY() {
        CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, flipper.getSelectEntity().year);
        calendar.set(Calendar.MONTH, flipper.getSelectEntity().month);
        calendar.set(Calendar.DAY_OF_MONTH, flipper.getSelectEntity().day);
        calendar.add(Calendar.DATE, flipper.weekOffsetCount);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int curBindYear = curBindCalendar.get(Calendar.YEAR);
        int curBindMonth = curBindCalendar.get(Calendar.MONTH);

        boolean leftFlag = false;
        boolean rightFlag = false;

        if (year < firstDayTimeEntity.year || (year == firstDayTimeEntity.year && month < firstDayTimeEntity.month) || (year == firstDayTimeEntity.year && month == firstDayTimeEntity.month && day < firstDayTimeEntity.day))
            leftFlag = false;
        else
            leftFlag = true;


        if (year > lastDayTimeEntity.year || (year == lastDayTimeEntity.year && month > lastDayTimeEntity.month) || (year == lastDayTimeEntity.year && month == lastDayTimeEntity.month && day > lastDayTimeEntity.day))
            rightFlag = false;
        else
            rightFlag = true;

        if (leftFlag && rightFlag) {
            int selectWeekNumOfMonth = 0;
            if (year == curBindYear && month == curBindMonth)
                selectWeekNumOfMonth = getNumSelectWeekOfMonth(year, month, day);

            Log.e("week", "update next:  weekNum = " + selectWeekNumOfMonth);
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
        } else {
            return 0;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        CalendarViewFlipper flipper = (CalendarViewFlipper) getParent();
        if (isFirstInitFlag && flipper.currentMode == MODE_WEEK) {
            dateLL.setTranslationY(-getFlipperTransLateY());
        } else if (flipper.currentMode == MODE_WEEK) {
            Log.e("week", "update next:  onLayout ");
            dateLL.setTranslationY(-getWeekTranslateY());
        }
    }
}
