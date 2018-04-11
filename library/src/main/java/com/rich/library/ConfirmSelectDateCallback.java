package com.rich.library;

/**
 * Created by richzjc on 18/3/14.
 */

public interface ConfirmSelectDateCallback {
    void selectSingleDate(DayTimeEntity timeEntity);

    void selectMultDate(DayTimeEntity startTimeEntity, DayTimeEntity endTimeEntity);
}
