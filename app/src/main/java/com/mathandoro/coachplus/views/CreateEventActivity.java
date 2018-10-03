package com.mathandoro.coachplus.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Location;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements  View.OnClickListener {

    public static final String INTENT_PARAM_TEAM = "team";

    DatePickerDialog datePickerDialog = null;
    TimePickerDialog timePickerDialog = null;
    private DataLayer dataLayer;
    EditText eventName;
    EditText description;

    Button startDate;
    Button startTime;
    Button endDate;
    Button endTime;

    Calendar start = Calendar.getInstance();
    Calendar end = Calendar.getInstance();


    Button createEventButton;
    Team team;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start.set(Calendar.SECOND, 0);
        end.set(Calendar.SECOND, 0);
        setContentView(R.layout.activity_create_event);
        dataLayer = DataLayer.getInstance(this);
        team = getIntent().getExtras().getParcelable(INTENT_PARAM_TEAM);

        eventName = (EditText)findViewById(R.id.createEventEventName);
        description = (EditText)findViewById(R.id.createEventEventDescription);
        startDate = (Button)findViewById(R.id.createEventEventStartDate);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker((Button)view, start);
            }
        });
        startTime = (Button)findViewById(R.id.createEventEventStartTime);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(startTime, start);
            }
        });
        endDate = (Button)findViewById(R.id.createEventEventEndDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker((Button)view, end);
            }
        });
        endTime = (Button)findViewById(R.id.createEventEventEndTime);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(endTime, end);
            }
        });
        createEventButton = (Button)findViewById(R.id.createEventCreateEventButton);
        createEventButton.setOnClickListener(this);
    }

    void showDatePicker(final Button view, final Calendar calendar){
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String monthText = ""+ month;
                if(month < 10){
                    monthText = "0" + month;
                }
                String dayText = "" + day;
                if(day < 10) {
                    dayText = "0"+ day;
                }
                view.setText(dayText + "." + monthText + "." + year);
                datePickerDialog.hide();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    void showTimePicker(final Button button, final Calendar calendar){
        Calendar mcurrentDate = Calendar.getInstance();
        int minute = mcurrentDate.get(Calendar.MINUTE);
        int hour = mcurrentDate.get(Calendar.HOUR);

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String hourText = ""+ hour;
                if(hour < 10){
                    hourText = "0" + hour;
                }
                String minuteText = "" + minute;
                if(minute < 10){
                    minuteText = "0"+minute;
                }
                button.setText(hourText + ":" + minuteText);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                timePickerDialog.hide();
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }


    @Override
    public void onClick(View view) {
        if(view == this.createEventButton){
            Date startDate = start.getTime();
            Date endDate = end.getTime();
            Location location = new Location("todo", 0, 0);
            Event newEvent = new Event(null, this.eventName.getText().toString(), this.description.getText().toString(), startDate, endDate, location);
            dataLayer.createEvent(team, newEvent, new DataLayerCallback<Event>() {
                @Override
                public void dataChanged(Event data) {
                    Log.i("create Event", "done");
                    finish();
                }

                @Override
                public void error() {
                    Log.i("create Event", "fail");
                }
            });
        }
    }
}
