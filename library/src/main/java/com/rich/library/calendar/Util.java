package com.rich.library.calendar;

import android.util.DisplayMetrics;
import android.content.Context;
public class Util {

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
}
