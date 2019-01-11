package com.gs.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateUtiles {
    //first<second
    public static int longOfTwoDate(Date first, Date second) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(first);
        int cnt = 0;
        while (!(calendar.getTime().getYear()==second.getYear()&&
                calendar.getTime().getMonth()==second.getMonth()&&
                calendar.getTime().getDate()==second.getDate())) {
            calendar.add(Calendar.DATE, 1);
            cnt++;
        }
        return cnt;
    }
}
