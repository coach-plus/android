package com.mathandoro.coachplus.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Formatter {
    public static String formatGermanTimestamp(Date startDate, Date endDate)
    {
        String dateString;
        String longFormat = "dd.MM.yy - HH:mm";
        String shortFormat = "HH:mm";
        SimpleDateFormat formatedStartDate = new SimpleDateFormat(longFormat);
        SimpleDateFormat formatedEndDate =  new SimpleDateFormat(longFormat);
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTime(endDate);
        endCalendar.setTime(endDate);

        if(startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)){
            formatedEndDate = new SimpleDateFormat(shortFormat);
        }

        try{
            dateString = formatedStartDate.format( startDate ) + " Uhr bis " + formatedEndDate.format(endDate) + " Uhr";
        }catch (Exception exception){
            dateString = "?";
        }
        return dateString;
    }
}
