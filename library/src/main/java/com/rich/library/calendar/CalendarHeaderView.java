package com.rich.library.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rich.library.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarHeaderView extends RelativeLayout {
    public TextView leftTitle;
    private View line;
    public CalendarSwitchView switchView;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");

    public void setLeftTitle(Calendar calendar) {
        leftTitle.setText(format.format(calendar.getTime()));
    }

    public CalendarHeaderView(Context context) {
        super(context);
        init(context);
    }

    public CalendarHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(getContext().getResources().getColor(R.color.day_mode_background_color1_ffffff));
        leftTitle = new TextView(context);
        leftTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        leftTitle.setTextColor(getResources().getColor(R.color.day_mode_text_color1_333333));
        leftTitle.setGravity(Gravity.CENTER);
        addView(leftTitle);

        line = new View(getContext());
        line.setBackgroundColor(getResources().getColor(R.color.day_mode_divide_line_color_e6e6e6));
        addView(line);

        switchView = new CalendarSwitchView(getContext());
        addView(switchView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int tvHeight = Util.dip2px(28f, getContext());
        int tvWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        int tvHeightSpec = MeasureSpec.makeMeasureSpec(tvHeight, MeasureSpec.EXACTLY);
        leftTitle.measure(tvWidthSpec, tvHeightSpec);

        int lineHeight = Util.dip2px(0.5f, getContext());
        int lineWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        int lineHeightSpec = MeasureSpec.makeMeasureSpec(lineHeight, MeasureSpec.EXACTLY);
        line.measure(lineWidthSpec, lineHeightSpec);

        int switchWidthSpec = MeasureSpec.makeMeasureSpec(Util.dip2px(84f, getContext()), MeasureSpec.EXACTLY);
        switchView.measure(switchWidthSpec, tvHeightSpec);

        int totalHeight = tvHeight + Util.dip2px(15f, getContext());
        setMeasuredDimension(widthSize, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int leftPadding = Util.dip2px(15f, getContext());
        leftTitle.layout(leftPadding, 0, leftPadding + leftTitle.getMeasuredWidth(), leftTitle.getMeasuredHeight());
        line.layout(0, getMeasuredHeight() - line.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        int switchStartX = getMeasuredWidth() - leftPadding - switchView.getMeasuredWidth();
        switchView.layout(switchStartX, 0, switchStartX + switchView.getMeasuredWidth(), switchView.getMeasuredHeight());
    }
}
