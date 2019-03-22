package com.mathandoro.coachplus.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Location;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements  View.OnClickListener, ToolbarFragment.ToolbarFragmentListener {

    public static final String INTENT_PARAM_TEAM = "team";
    public static final String INTENT_PARAM_EVENT = "event";

    DatePickerDialog datePickerDialog = null;
    TimePickerDialog timePickerDialog = null;
    private DataLayer dataLayer;

    TextInputEditText nameInput;
    TextInputEditText locationInput;
    TextInputEditText descriptionInput;

    Button startDate;
    Button startTime;
    Button endDate;
    Button endTime;
    Button deleteEventButton;

    Calendar eventStartCalendar = Calendar.getInstance();
    Calendar eventEndCalendar = Calendar.getInstance();

    private boolean initialChange = true;

    Team team;
    Event event;

    boolean editMode = false;

    FloatingActionButton createEventButton;
    private ToolbarFragment toolbarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);

        eventStartCalendar.set(Calendar.SECOND, 0);
        eventEndCalendar.set(Calendar.SECOND, 0);

        dataLayer = DataLayer.getInstance(this);

        team = getIntent().getExtras().getParcelable(INTENT_PARAM_TEAM);
        event = getIntent().getExtras().getParcelable(INTENT_PARAM_EVENT);
        editMode = event != null;

        // todo if editmode, show save & delete event buttons

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.create_event_activity_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();

        nameInput = findViewById(R.id.create_event_event_title_text_input);
        locationInput = findViewById(R.id.create_event_event_location_text_input);
        descriptionInput = findViewById(R.id.create_event_event_description_text_input);

        startDate = findViewById(R.id.create_event_start_date_picker_button);
        startDate.setOnClickListener(view -> showDatePicker((Button)view, eventStartCalendar));
        startTime = findViewById(R.id.create_event_start_time_picker_button);
        startTime.setOnClickListener(view -> showTimePicker(startTime, eventStartCalendar));

        endDate = findViewById(R.id.create_event_end_date_picker_button);
        endDate.setOnClickListener(view -> showDatePicker((Button)view, eventEndCalendar));
        endTime = findViewById(R.id.create_event_end_time_picker_button);
        endTime.setOnClickListener(view -> showTimePicker(endTime, eventEndCalendar));

        createEventButton = findViewById(R.id.create_event_create_button);
        createEventButton.setOnClickListener(this);

        deleteEventButton = findViewById(R.id.create_event_delete_button);
        // todo delete event

        if(editMode){
            toolbarFragment.setTitle(getString(R.string.edit_event_title));

            // createEventButton.// todo load data
        }
        else{
            toolbarFragment.setTitle(getString(R.string.create_event_title));

            deleteEventButton.setVisibility(View.GONE);
            updateDateAndTimeUI();
        }
    }

    void showDatePicker(final Button dateSelectionButton, final Calendar calendar){
        Calendar currentDateCalendar = Calendar.getInstance();
        int currentYear = currentDateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDateCalendar.get(Calendar.MONTH);
        int currentDay = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            setCalendarDate(calendar, selectedYear, selectedMonth, selectedDay);
            dateSelectionButton.setText(formatDate(calendar));
            handleCalendarOverlaps(calendar);
            datePickerDialog.hide();
        }, currentYear, currentMonth, currentDay);
        String title = calendar == eventStartCalendar ? "select start date" : "select end date";
        datePickerDialog.setTitle(title);
        datePickerDialog.show();
    }

    void showTimePicker(final Button timeSelectionButton, final Calendar calendar){
        Calendar currentDate = Calendar.getInstance();
        int currentMinute = currentDate.get(Calendar.MINUTE);
        int currentHour = currentDate.get(Calendar.HOUR);

        timePickerDialog = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            setCalendarTime(calendar, selectedHour , selectedMinute);
            handleCalendarOverlaps(calendar);
            timePickerDialog.hide();
        }, currentHour, currentMinute, true);
        String title = calendar == eventStartCalendar ? "select start time" : "select end time";
        timePickerDialog.setTitle(title);
        timePickerDialog.show();
    }

    private void handleCalendarOverlaps(Calendar changedCalendar){
        if(changedCalendar == eventStartCalendar && initialChange){
            initialChange = false;
            copyCalendar(eventEndCalendar, eventStartCalendar);
            eventEndCalendar.add(Calendar.MINUTE, 90);
            updateDateAndTimeUI();
        }
        else if(changedCalendar == eventStartCalendar && eventEndCalendar.before(eventStartCalendar)){
            copyCalendar(eventEndCalendar, eventStartCalendar);
            eventEndCalendar.add(Calendar.MINUTE, 90);
            updateDateAndTimeUI();
        }
        else if(changedCalendar == eventEndCalendar && eventEndCalendar.before(eventStartCalendar)){
            copyCalendar( eventStartCalendar, eventEndCalendar);

            eventStartCalendar.add(Calendar.MINUTE, -90);
            updateDateAndTimeUI();
        }
    }

    private void copyCalendar(Calendar target, Calendar source){
        target.set(
                source.get(Calendar.YEAR),
                source.get(Calendar.MONTH),
                source.get(Calendar.DATE),
                source.get(Calendar.HOUR_OF_DAY),
                source.get(Calendar.MINUTE));
    }

    private void updateDateAndTimeUI(){
        startDate.setText(formatDate(eventStartCalendar));
        startTime.setText(formatTime(eventStartCalendar));
        endDate.setText(formatDate(eventEndCalendar));
        endTime.setText(formatTime(eventEndCalendar));
    }

    private void setCalendarDate(Calendar calendar, int year, int month, int day){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    private void setCalendarTime(Calendar calendar, int hour, int minute){
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        updateDateAndTimeUI();
    }

    private CharSequence formatDate(Calendar calendar){
        return DateFormat.format("EEE, dd. MMMM yyyy", calendar.getTime());
        //return DateFormat.getLongDateFormat(getApplicationContext()).format(calendar.getTime());
    }

    private String formatTime(Calendar calendar){
        return DateFormat.getTimeFormat(getApplicationContext()).format(calendar.getTime());
    }

    private boolean isValid(){
        if(nameInput.getText().toString().trim().equals("")){
            nameInput.setError(getString(R.string.name_is_required_error));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == this.createEventButton){
            Date startDate = eventStartCalendar.getTime();
            Date endDate = eventEndCalendar.getTime();

            if(!this.isValid()){
                return;
            }

            // todo location (google maps picker?)
            Location location = new Location(locationInput.getText().toString(), 0, 0);
            Event newEvent = new Event(null, this.nameInput.getText().toString(),
                    this.descriptionInput.getText().toString(), startDate, endDate, location);

            // todo use observable
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

    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {

    }
}
