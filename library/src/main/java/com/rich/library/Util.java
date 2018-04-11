package com.rich.library;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richzjc on 18/3/13.
 */

public class Util {

    private static List<MonthTimeEntity> initMonthList(Calendar startCalendar, Calendar endCalendar) {
        List<MonthTimeEntity> monthTimeEntities = new ArrayList<>();
        monthTimeEntities.clear();
        if (endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis())
            throw new IllegalStateException("结束时间不能早于开始时间");
        else {
            int startYear = startCalendar.get(Calendar.YEAR);
            int startMonth = startCalendar.get(Calendar.MONTH);
            int endYear = endCalendar.get(Calendar.YEAR);
            int endMonth = endCalendar.get(Calendar.MONTH);

            if (startYear == endYear && startMonth == endMonth) {
                monthTimeEntities.add(new MonthTimeEntity(startYear, startMonth));
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startCalendar.getTimeInMillis());
                while (true) {
                    int tempYear = calendar.get(Calendar.YEAR);
                    int tempMonth = calendar.get(Calendar.MONTH);
                    monthTimeEntities.add(new MonthTimeEntity(tempYear, tempMonth));
                    if (tempYear == endYear && tempMonth == endMonth)
                        break;
                    else
                        calendar.add(Calendar.MONTH, 1);
                }
            }
        }
        return monthTimeEntities;
    }

    public static List<DayTimeEntity> getListByMonthTime(MonthTimeEntity monthTimeEntity, int monthPosition) {
        List<DayTimeEntity> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, monthTimeEntity.year);
        calendar.set(Calendar.MONTH, monthTimeEntity.month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int position = monthPosition + 1;

        for (int i = 0; i < dayOfWeek - 1; i++) {
            list.add(new DayTimeEntity(monthTimeEntity.year, monthTimeEntity.month, 0, position ++, monthPosition));
        }
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        for (int i = 1; i <= calendar.get(Calendar.DAY_OF_MONTH); i++) {
            list.add(new DayTimeEntity(monthTimeEntity.year, monthTimeEntity.month, i, position ++, monthPosition));
        }

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < 7 - dayOfWeek; i++) {
            list.add(new DayTimeEntity(monthTimeEntity.year, monthTimeEntity.month, 0, position ++, monthPosition));
        }
        return list;
    }

    public static List<Object> getTotalCount(Calendar startCalendar, Calendar endCalendar) {
        List<Object> list = new ArrayList<>(2);
        Map<Integer, MonthTimeEntity> map = new LinkedHashMap<>();
        int totalCount = 0;
        List<MonthTimeEntity> timeEntities = initMonthList(startCalendar, endCalendar);
        for (MonthTimeEntity entity : timeEntities) {
            totalCount++;
            map.put(totalCount - 1, entity);
            totalCount = totalCount + getDayCountInMonth(entity);
        }
        list.add(totalCount);
        list.add(map);
        return list;
    }

    private static int getDayCountInMonth(MonthTimeEntity monthTimeEntity) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, monthTimeEntity.year);
        calendar.set(Calendar.MONTH, monthTimeEntity.month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int totalCount = 0;
        totalCount = totalCount + (dayOfWeek - 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        totalCount = totalCount + calendar.get(Calendar.DAY_OF_MONTH);
        totalCount = totalCount + (7 - calendar.get(Calendar.DAY_OF_WEEK));
        return totalCount;
    }

    /**
     * 月日时分秒，0-9前补0
     */
    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    public static int preCount(DayTimeEntity dayTimeEntity){
        if(dayTimeEntity == null || dayTimeEntity.day == 0)
            return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dayTimeEntity.year);
        calendar.set(Calendar.MONTH, dayTimeEntity.month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static void setKeywords(String titleStr, TextView textView, String editTextColor, int color) {
        if (TextUtils.isEmpty(titleStr)) {
            textView.setText("");
            return;
        }

        if (TextUtils.isEmpty(editTextColor)) {
            textView.setText(titleStr);
            return;
        }

        SpannableStringBuilder style = new SpannableStringBuilder(titleStr);
        int site = titleStr.indexOf(editTextColor);
        if (site == -1) {
            site = titleStr.toUpperCase().indexOf(editTextColor);
            if (site == -1) {
                site = titleStr.toLowerCase().indexOf(editTextColor);
            }
        }
        if (site >= 0) {
            style.setSpan(new ForegroundColorSpan(color), site, site + editTextColor.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);   //关键字红色显示
        }
        textView.setText(style);
    }


    public static void setKeywordsSize(String titleStr, TextView tv, String editText, int textSize) {
        if (TextUtils.isEmpty(titleStr)) {
            tv.setText("");
            return;
        }

        SpannableStringBuilder style = new SpannableStringBuilder(titleStr);
        int index = titleStr.indexOf(editText);
        style.setSpan(new AbsoluteSizeSpan(textSize, true), index, index + editText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(style);
    }
}
