package com.rich.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by richzjc on 18/3/13.
 */

public class CalendarSelectView extends LinearLayout {

    private Context context;

    public static       String START_TIME_KEY = "startTime";
    public static       String END_TIME_KEY   = "endTime";
    public static final int    SINGLE         = 1;
    public static final int    MULT           = 2;

    private final int START = 0;
    private final int TODAY = 1;
    private final int END   = 2;


    private TextView            leftTime;
    private TextView            rightTime;
    private RecyclerView        recyclerView;
    private TextView            define;
    private LinearLayout        timeParent;
    private TextView            clear;
    private TextView            confirm;
    private OuterRecycleAdapter outAdapter;

    private int selectType;
    private int locationType;

    Calendar          startCalendar;
    Calendar          endCalendar;
    Calendar          startCalendarDate;
    Calendar          endCalendarDate;
    DayTimeEntity     startDayTime;
    DayTimeEntity     endDayTime;
    GridLayoutManager layoutManager;

    private ConfirmSelectDateCallback selectDateCallback;
    private CalendarSelectUpdateCallback multCallback = new CalendarSelectUpdateCallback() {
        @Override
        public void updateMultView() {
            CalendarSelectView.this.updateMultView();
        }

        @Override
        public void refreshLocate(int position) {
            try {
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(position, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public CalendarSelectView(Context context) {
        this(context, null);
    }

    public CalendarSelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCalendar();
        initView(context);
        initAttrs(attrs);
        initAdapter();
        addListener();
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        startDayTime = new DayTimeEntity(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 0, -1, -1);
        endDayTime = new DayTimeEntity(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 0, -1, -1);

        startCalendarDate = Calendar.getInstance();
        startCalendarDate.add(Calendar.YEAR, -1);
        startCalendarDate.set(Calendar.DATE, 1);
        endCalendarDate = Calendar.getInstance();
        endCalendarDate.add(Calendar.MONTH, 3);
        endCalendarDate.set(Calendar.DATE, 1);
        endCalendarDate.add(Calendar.MONTH, 1);
        endCalendarDate.add(Calendar.DATE, -1);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        initStartEndCalendar();
    }

    private void initStartEndCalendar() {
        startCalendar.setTimeInMillis(startCalendarDate.getTimeInMillis());
        endCalendar.setTimeInMillis(endCalendarDate.getTimeInMillis());

        startCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
        endCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
        startCalendarDate.set(Calendar.MINUTE, 0);
        endCalendarDate.set(Calendar.MINUTE, 0);
        startCalendarDate.set(Calendar.SECOND, 0);
        endCalendarDate.set(Calendar.SECOND, 0);
        startCalendarDate.set(Calendar.MILLISECOND, 0);
        endCalendarDate.set(Calendar.MILLISECOND, 0);
    }

    private void initView(Context context) {
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.global_view_calendar_select, this, true);
        int color = ContextCompat.getColor(context, R.color.day_mode_background_color);
        setBackgroundColor(color);
        leftTime = findViewById(R.id.left_time);
        rightTime = findViewById(R.id.right_time);
        recyclerView = findViewById(R.id.recycleView);
        define = findViewById(R.id.define);
        timeParent = findViewById(R.id.time_parent);
        clear = findViewById(R.id.clear);
        confirm = findViewById(R.id.confirm);
    }

    private void initAdapter() {
        layoutManager =
                new GridLayoutManager(getContext(),
                        7,
                        GridLayoutManager.VERTICAL,
                        false
                );
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (outAdapter != null && outAdapter.getMap() != null) {
                    Map<Integer, MonthTimeEntity> map = outAdapter.getMap();
                    if (map.containsKey(position))
                        return 7;
                    else
                        return 1;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration());
        outAdapter = new OuterRecycleAdapter(Util.getTotalCount(startCalendar, endCalendar), selectType,
                startCalendarDate, endCalendarDate,
                startDayTime, endDayTime);
        outAdapter.setUpdateMultCallback(multCallback);
        recyclerView.setAdapter(outAdapter);
        outAdapter.scrollToPosition();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.calendarSelect);
            selectType = array.getInt(R.styleable.calendarSelect_select_type, SINGLE);
            updateViewVisibility();
            locationType = array.getInt(R.styleable.calendarSelect_locate_position, START);
            updateDayTimeEntity();
            array.recycle();
        }
    }

    private void updateDayTimeEntity() {
        if (locationType == TODAY) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() <= endCalendarDate.getTimeInMillis()) {
                endDayTime.year = calendar.get(Calendar.YEAR);
                endDayTime.month = calendar.get(Calendar.MONTH);
                endDayTime.day = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                endDayTime.year = endCalendarDate.get(Calendar.YEAR);
                endDayTime.month = endCalendarDate.get(Calendar.MONTH);
                endDayTime.day = endCalendarDate.get(Calendar.DAY_OF_MONTH);
            }
        } else if (locationType == END) {
            endDayTime.year = endCalendarDate.get(Calendar.YEAR);
            endDayTime.month = endCalendarDate.get(Calendar.MONTH);
            endDayTime.day = endCalendarDate.get(Calendar.DAY_OF_MONTH);
        } else {
            endDayTime.year = startCalendarDate.get(Calendar.YEAR);
            endDayTime.month = startCalendarDate.get(Calendar.MONTH);
            endDayTime.day = startCalendarDate.get(Calendar.DAY_OF_MONTH);
        }

        if (endDayTime.day != 0 && (selectType == SINGLE)) {
            startDayTime.year = endDayTime.year;
            startDayTime.month = endDayTime.month;
            startDayTime.day = endDayTime.day;
        }
        updateMultView();
    }

    private void updateMultView() {
        if (selectType == MULT) {
            if (startDayTime.day != 0) {
                leftTime.setText(startDayTime.year + "-" + Util.fillZero(startDayTime.month + 1) + "-" + Util.fillZero(startDayTime.day));
            } else {
                leftTime.setText("起始日期");
            }

            if (endDayTime.day != 0) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DATE);
                String value = endDayTime.year + "-" + Util.fillZero(endDayTime.month + 1) + "-" + Util.fillZero(endDayTime.day);
                if ((year == endDayTime.year) && (month == endDayTime.month) && (day == endDayTime.day)) {
                    int color = ContextCompat.getColor(getContext(), R.color.day_mode_text_color_999999);
                    Util.setKeywords(value + " 今天", rightTime, "今天", color);
                } else {
                    rightTime.setText(value);
                }
            } else {
                rightTime.setText("结束日期");
            }
        }
    }

    public void setCalendarRange(Calendar startCalendar, Calendar endCalendar, DayTimeEntity startDayTime, DayTimeEntity endDayTime) {
        if (startCalendar == null || endCalendar == null)
            throw new IllegalStateException("传入的日历是不能为空的");
        else if (endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis())
            throw new IllegalStateException("结束日期不能早于开始日期");
        else {
            this.startCalendarDate.setTimeInMillis(startCalendar.getTimeInMillis());
            this.endCalendarDate.setTimeInMillis(endCalendar.getTimeInMillis());
            initStartEndCalendar();
            setStartEndTime(startDayTime, endDayTime);
            if (outAdapter != null)
                outAdapter.setData(Util.getTotalCount(startCalendar, endCalendar));
        }
    }

    public void setStartEndTime(DayTimeEntity startDayTime, DayTimeEntity endDayTime) {
        if (startDayTime == null || endDayTime == null) {
            updateDayTimeEntity();
        } else {
            if (startDayTime != null) {
                this.startDayTime.day = startDayTime.day;
                this.startDayTime.year = startDayTime.year;
                this.startDayTime.monthPosition = -1;
                this.startDayTime.month = startDayTime.month;
                this.startDayTime.listPosition = -1;
            }

            if (endDayTime != null) {
                this.endDayTime.day = endDayTime.day;
                this.endDayTime.year = endDayTime.year;
                this.endDayTime.monthPosition = -1;
                this.endDayTime.month = endDayTime.month;
                this.endDayTime.listPosition = -1;
            }
        }

        updateMultView();
        if (outAdapter != null){
            outAdapter.notifyDataSetChanged();
            outAdapter.scrollToLocation();
        }
    }

    private void updateViewVisibility() {
        if (selectType == SINGLE) {
            define.setVisibility(View.GONE);
            timeParent.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
        } else if (selectType == MULT) {
            define.setVisibility(View.VISIBLE);
            timeParent.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
        }
    }

    private void addListener() {
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startDayTime.day = 0;
                endDayTime.day = 0;
                startDayTime.listPosition = -1;
                startDayTime.monthPosition = -1;
                endDayTime.listPosition = -1;
                endDayTime.monthPosition = -1;
                leftTime.setText("起始时间");
                rightTime.setText("结束时间");
                if (outAdapter != null)
                    outAdapter.notifyDataSetChanged();
            }
        });

        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDateCallback != null) {
                    if (selectType == SINGLE)
                        selectDateCallback.selectSingleDate(startDayTime);
                    else if (selectType == MULT) {
                        selectDateCallback.selectMultDate(startDayTime, endDayTime);
                    }
                }
            }
        });
    }

    public void setConfirmCallback(ConfirmSelectDateCallback selectDateCallback) {
        this.selectDateCallback = selectDateCallback;
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        public SpaceItemDecoration() {

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 10;
            int position = parent.getChildLayoutPosition(view);
            if (outAdapter != null && outAdapter.getMap() != null) {
                Map<Integer, MonthTimeEntity> map = outAdapter.getMap();
                if (map.containsKey(position))
                    outRect.top = 20;
                else
                    outRect.top = 0;
            } else {
                outRect.top = 0;
            }
        }
    }
}
