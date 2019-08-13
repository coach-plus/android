package com.mathandoro.coachplus.helpers;

import android.content.Context;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.ReducedUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Formatter {
    public static String formatGermanTimestamp(Date startDate, Context context)
    {
        return formatGermanTimestamp(startDate, null , context);
    }

    public static String formatGermanTimestamp(Date startDate, Date endDate, Context context)
    {
        String dateString;
        String longFormat = "EE., dd.MM.yy - HH:mm";
        String shortFormat = "HH:mm";
        SimpleDateFormat formatedStartDate = new SimpleDateFormat(longFormat);
        Calendar startCalendar = Calendar.getInstance();

        startCalendar.setTime(startDate);

        dateString = formatedStartDate.format( startDate ) ;
        if(endDate != null){
            Calendar endCalendar = Calendar.getInstance();
            SimpleDateFormat formatedEndDate =  new SimpleDateFormat(longFormat);
            endCalendar.setTime(endDate);
            if(startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)){
                formatedEndDate = new SimpleDateFormat(shortFormat);
            }
            String to = context.getString(R.string.to);
            dateString += " " + to + " " + formatedEndDate.format(endDate);
        }
        return dateString;
    }

    public static String formatUserName(ReducedUser user){
        return user.getFirstname() + " " + user.getLastname();
    }

    public static String formatMonthDay(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

    public static String formatfullMonthName(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        return dateFormat.format(date);
    }
}
