package com.rich.calendar;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class CalendarSelectAcitivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String flag = getIntent().getStringExtra("flag");
        if(TextUtils.equals(flag, "single"))
            setContentView(R.layout.select_date_single);
       else if(TextUtils.equals(flag, "calendar_new"))
           setContentView(R.layout.select_date_calendar_new);
        else
            setContentView(R.layout.select_date_mult);
    }
}
