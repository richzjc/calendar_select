package com.rich.library;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richzjc on 18/3/13.
 */

public class OuterRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int                               totalCount;
    Map<Integer, MonthTimeEntity>     map;
    Map<Integer, List<DayTimeEntity>> listMap;
    Calendar                          startCalendarDate;
    Calendar                          endCalendarDate;
    DayTimeEntity                     startDayTime;
    DayTimeEntity                     endDayTime;
    int                               selectType;
    public CalendarSelectUpdateCallback multCallback;

    public OuterRecycleAdapter(List<Object> list, int selectType,
                               Calendar startCalendarDate, Calendar endCalendarDate,
                               DayTimeEntity startDayTime, DayTimeEntity endDayTime) {
        try {
            listMap = new HashMap<>();
            this.startCalendarDate = startCalendarDate;
            this.endCalendarDate = endCalendarDate;
            this.startDayTime = startDayTime;
            this.endDayTime = endDayTime;
            this.selectType = selectType;
            if (list != null && list.size() == 2) {
                totalCount = (int) list.get(0);
                map = (Map<Integer, MonthTimeEntity>) list.get(1);
                calculateListPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(List<Object> list) {
        try {
            if (list != null && list.size() == 2) {
                totalCount = (int) list.get(0);
                map = (Map<Integer, MonthTimeEntity>) list.get(1);
                listMap.clear();
                calculateListPosition();
                notifyDataSetChanged();
                scrollToPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateListPosition() {
        if (startDayTime.day != 0) {
            for (Integer key : map.keySet()) {
                MonthTimeEntity timeEntity = map.get(key);
                if ((timeEntity.year == startDayTime.year) && (timeEntity.month == startDayTime.month)) {
                    startDayTime.listPosition = key + Util.preCount(startDayTime) + startDayTime.day;
                    startDayTime.monthPosition = key;
                    break;
                }
            }
        }

        if (endDayTime.day != 0) {
            for (Integer key : map.keySet()) {
                MonthTimeEntity timeEntity = map.get(key);
                if ((timeEntity.year == endDayTime.year) && (timeEntity.month == endDayTime.month)) {
                    endDayTime.listPosition = key + Util.preCount(endDayTime) + endDayTime.day;
                    endDayTime.monthPosition = key;
                    break;
                }
            }
        }
    }

    public void scrollToPosition() {
        if (multCallback != null) {
            if (startDayTime.day != 0)
                multCallback.refreshLocate(startDayTime.monthPosition);
            else if (endDayTime.day != 0)
                multCallback.refreshLocate(endDayTime.monthPosition);
        }
    }

    public void scrollToLocation(){
        calculateListPosition();
        scrollToPosition();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_recycler_item_outer, parent, false);
            return new OuterRecycleViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_recycler_item_inner, parent, false);
            InnerViewHolder holder = new InnerViewHolder(view, startCalendarDate, endCalendarDate, startDayTime, endDayTime);
            DateOnclickListener listener = new DateOnclickListener(selectType, startDayTime, endDayTime, this);
            holder.itemView.setOnClickListener(listener);
            holder.itemView.setTag(listener);
            return holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (map.containsKey(position))
            return 1;
        else
            return 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OuterRecycleViewHolder) {
            ((OuterRecycleViewHolder) holder).doBindData(map.get(position));
        } else if (holder instanceof InnerViewHolder) {
            int monthPosition = getMonthPosition(position);
            List<DayTimeEntity> list;
            if (listMap.containsKey(monthPosition)) {
                list = listMap.get(monthPosition);
            } else {
                list = Util.getListByMonthTime(map.get(monthPosition), monthPosition);
                listMap.put(monthPosition, list);
            }
            int pos = position - monthPosition - 1;
            ((InnerViewHolder) holder).doBindData(list.get(pos));
            DateOnclickListener listener = (DateOnclickListener) holder.itemView.getTag();
            if (listener != null) {
                listener.setEntity(list.get(pos));
            }
        }
    }

    private int getMonthPosition(int position) {
        int lastKey = -1;
        for (Integer key : map.keySet()) {
            if (lastKey == -1) {
                lastKey = key;
            } else if (position > lastKey && position < key) {
                break;
            } else {
                lastKey = key;
            }
        }
        return lastKey;
    }

    @Override
    public int getItemCount() {
        return totalCount;
    }

    public Map<Integer, MonthTimeEntity> getMap() {
        return map;
    }

    public void setUpdateMultCallback(CalendarSelectUpdateCallback multCallback) {
        this.multCallback = multCallback;
    }
}
