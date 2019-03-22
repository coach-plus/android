package com.mathandoro.coachplus.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.Disposable;

import android.content.Intent;
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
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    public static final String INTENT_PARAM_TEAM = "team";
    public static final String INTENT_PARAM_EVENT = "event";
    public static final String RETURN_INTENT_PARAM_ACTION = "action";
    public static final String RETURN_INTENT_PARAM_EVENT = "event";


    public static final String ACTION_CREATED = "created";
    public static final String ACTION_UPDATED = "updated";
    public static final String ACTION_DELETED = "deleted";



    private DatePickerDialog datePickerDialog = null;
    private TimePickerDialog timePickerDialog = null;
    private DataLayer dataLayer;

    private TextInputEditText nameInput;
    private TextInputEditText locationInput;
    private TextInputEditText descriptionInput;

    private Button startDate;
    private Button startTime;
    private Button endDate;
    private Button endTime;
    private Button deleteEventButton;

    private Calendar eventStartCalendar = Calendar.getInstance();
    private Calendar eventEndCalendar = Calendar.getInstance();

    private boolean initialChange = true;
    private boolean editMode = false;

    private Team team;
    private Event event;

    private FloatingActionButton saveEventButton;
    private ToolbarFragment toolbarFragment;
    private int DEFAULT_EVENT_DURATION = 90; // minutes


    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() { }


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
        startTime.setOnClickListener(view -> showTimePicker(eventStartCalendar));

        endDate = findViewById(R.id.create_event_end_date_picker_button);
        endDate.setOnClickListener(view -> showDatePicker((Button)view, eventEndCalendar));
        endTime = findViewById(R.id.create_event_end_time_picker_button);
        endTime.setOnClickListener(view -> showTimePicker(eventEndCalendar));

        saveEventButton = findViewById(R.id.create_event_create_button);
        saveEventButton.setOnClickListener(view -> save());

        deleteEventButton = findViewById(R.id.create_event_delete_button);
        deleteEventButton.setOnClickListener((view) -> this.deleteEvent());

        if(editMode){
            toolbarFragment.setTitle(getString(R.string.edit_event_title));
            fillFormWithExistingEvent();
        }
        else{
            toolbarFragment.setTitle(getString(R.string.create_event_title));

            deleteEventButton.setVisibility(View.GONE);
            updateDateAndTimeUI();
        }
    }

    private void fillFormWithExistingEvent(){
        nameInput.setText(event.getName());
        locationInput.setText(event.getLocation().getName());
        descriptionInput.setText(event.getDescription());
        eventStartCalendar.setTime(event.getStart());
        eventEndCalendar.setTime(event.getEnd());
        updateDateAndTimeUI();
    }

    private void showDatePicker(final Button dateSelectionButton, final Calendar calendar){
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

    private void showTimePicker(final Calendar calendar){
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
            eventEndCalendar.add(Calendar.MINUTE, DEFAULT_EVENT_DURATION);
            updateDateAndTimeUI();
        }
        else if(changedCalendar == eventStartCalendar && eventEndCalendar.before(eventStartCalendar)){
            copyCalendar(eventEndCalendar, eventStartCalendar);
            eventEndCalendar.add(Calendar.MINUTE, DEFAULT_EVENT_DURATION);
            updateDateAndTimeUI();
        }
        else if(changedCalendar == eventEndCalendar && eventEndCalendar.before(eventStartCalendar)){
            copyCalendar( eventStartCalendar, eventEndCalendar);

            eventStartCalendar.add(Calendar.MINUTE, -DEFAULT_EVENT_DURATION);
            updateDateAndTimeUI();
        }
    }

    private void copyCalendar(Calendar target, Calendar source){
        target.set(source.get(Calendar.YEAR),
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

    private Event getEventFromForm(){
        Date startDate = eventStartCalendar.getTime();
        Date endDate = eventEndCalendar.getTime();

        if(!this.isValid()){
            return null;
        }

        Location location = new Location(locationInput.getText().toString(), 0, 0);
        String eventId = editMode ? event.get_id() : null;
        Event newEvent = new Event(eventId, this.nameInput.getText().toString(),
                this.descriptionInput.getText().toString(), startDate, endDate, location);
        return newEvent;
    }

    private void save(){
        Event event = getEventFromForm();
        if(event == null){
            return;
        }
        if(editMode){
            this.updateEvent(event);
        }
        else {
            this.createEvent(event);
        }
    }

    private void createEvent(Event newEvent){
        // todo location (google maps picker?)
        dataLayer.createEvent(team.get_id(), newEvent)
                .subscribe((res) -> success(ACTION_CREATED, res.getEvent()),
                        (error) -> handleApiCallError(error));
    }

    private void updateEvent(Event updatedEvent){
        dataLayer.updateEvent(team.get_id(), updatedEvent)
                .subscribe((res) -> success(ACTION_UPDATED, res.getEvent()),
                        (error) -> handleApiCallError(error));
    }

    private void deleteEvent(){
        dataLayer.deleteEvent(team.get_id(), event.get_id())
                .subscribe((res) -> success(ACTION_DELETED, null),
                        (error) -> handleApiCallError(error));
    }

    private void handleApiCallError(Throwable error){
        Log.i("create Event", "fail");
    }

    private void success(String action, Event event){
        Intent result = new Intent();
        result.putExtra(RETURN_INTENT_PARAM_ACTION, action);
        result.putExtra(RETURN_INTENT_PARAM_EVENT, event);
        setResult(RESULT_OK, result);
        finish();
    }
}
