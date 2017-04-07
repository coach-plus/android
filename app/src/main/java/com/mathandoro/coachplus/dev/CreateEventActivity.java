package com.mathandoro.coachplus.dev;

import android.app.DatePickerDialog;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.mathandoro.coachplus.R;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DatePickerDialog datePickerDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        showDatePicker();
    }

    void showDatePicker(){
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(CreateEventActivity.this, this, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.d("test", "test");
        /*      Your code   to get date and time    */
        // lselectedmonth = selectedmonth + 1;
        datePickerDialog.hide();
        //eReminderDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
    }
}
