package com.rich.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by richzjc on 18/3/13.
 */

public class OuterRecycleViewHolder extends RecyclerView.ViewHolder {

    TextView txtMonth;

    public OuterRecycleViewHolder(View itemView) {
        super(itemView);
        txtMonth = itemView.findViewById(R.id.plan_time_txt_month);
    }

    public void doBindData(MonthTimeEntity timeEntity) {
        String title = itemView.getContext().getString(R.string.outer_title, String.valueOf(timeEntity.year), String.valueOf(timeEntity.month + 1));
        txtMonth.setText(title);
    }
}
