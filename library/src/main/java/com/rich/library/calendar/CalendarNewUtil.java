package com.rich.library.calendar;

import android.util.Log;

import com.rich.library.DayTimeEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class CalendarNewUtil {

    public static void initAllDayTimeEntity(Map<String, List<DayTimeEntity>> map, Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String key = format.format(calendar.getTime());
        List<DayTimeEntity> list = null;
        if (!map.containsKey(key)) {
            list = new ArrayList<>();
            int dayCount = getDayCountOfMonth(calendar);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            for (int i = 1; i <= dayCount; i++) {
                list.add(new DayTimeEntity(year, month, i, 0, 0));
            }
            map.put(key, list);
        }
    }

    public static int getDayCountOfMonth(Calendar calendar) {
        int day = 0;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }

        switch (month) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;
            case 3:
            case 5:
            case 8:
            case 10:
                return 30;
            case 1:
                return day;
        }

        return 0;
    }


    public static int getWeekCountOfMonth(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int dayCount = getDayCountOfMonth(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int preCount = 0;
        int cweek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek) {
            case 1:
                preCount = 7;
                break;
            case 2:
                preCount = 6;
                break;
            case 3:
                preCount = 5;
                break;
            case 4:
                preCount = 4;
                break;
            case 5:
                preCount = 3;
                break;
            case 6:
                preCount = 2;
                break;
            case 7:
                preCount = 1;
                break;
        }


        calendar.set(Calendar.DAY_OF_MONTH, dayCount);

        int lastCount = 0;
        int cweek1 = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek1) {
            case 1:
                lastCount = 1;
                break;
            case 2:
                lastCount = 2;
                break;
            case 3:
                lastCount = 3;
                break;
            case 4:
                lastCount = 4;
                break;
            case 5:
                lastCount = 5;
                break;
            case 6:
                lastCount = 6;
                break;
            case 7:
                lastCount = 7;
                break;
        }

        int weekCount = 2 + (dayCount - preCount - lastCount) / 7;
        return weekCount;
    }



    public static int firstStartIndex(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int preCount = 0;
        int cweek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek) {
            case 1:
                preCount = 0;
                break;
            case 2:
                preCount = 1;
                break;
            case 3:
                preCount = 2;
                break;
            case 4:
                preCount = 3;
                break;
            case 5:
                preCount = 4;
                break;
            case 6:
                preCount = 5;
                break;
            case 7:
                preCount = 6;
                break;
        }

        return preCount;
    }

    public static int lastEndIndex(Calendar calendar, int totalCount) {
        calendar.set(Calendar.DAY_OF_MONTH, totalCount);

        int preCount = 0;
        int cweek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (cweek) {
            case 1:
                preCount = 6;
                break;
            case 2:
                preCount = 5;
                break;
            case 3:
                preCount = 4;
                break;
            case 4:
                preCount = 3;
                break;
            case 5:
                preCount = 2;
                break;
            case 6:
                preCount = 1;
                break;
            case 7:
                preCount = 0;
                break;
        }

        return preCount;
    }
}
