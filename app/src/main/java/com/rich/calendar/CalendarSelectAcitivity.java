package com.rich.calendar;

import static com.rich.library.calendar.CalendarViewFlipper.MODE_WEEK;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.rich.library.calendar.CalendarTotalView;

import java.util.Calendar;

public class CalendarSelectAcitivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String flag = getIntent().getStringExtra("flag");
        if (TextUtils.equals(flag, "single"))
            setContentView(R.layout.select_date_single);
        else if (TextUtils.equals(flag, "calendar_new")) {
            setContentView(R.layout.select_date_calendar_new);
            CalendarTotalView selectNewView = findViewById(R.id.calendar_select);
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.MONTH, -3);
            endCalendar.add(Calendar.MONTH, 3);
            selectNewView.setCalendarRange(startCalendar, endCalendar, MODE_WEEK);
        } else
            setContentView(R.layout.select_date_mult);
    }
}
