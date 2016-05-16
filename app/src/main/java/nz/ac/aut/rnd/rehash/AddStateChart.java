package nz.ac.aut.rnd.rehash;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddStateChart extends AppCompatActivity {
    private EditText titleET;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private EditText descriptionText;
    private Button addEventButton;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_state_chart);

        ref = ((Rehash)getApplication()).getFirebaseRef();

        titleET = (EditText) findViewById(R.id.add_event_title);
        startDateButton = (Button) findViewById(R.id.add_event_start_date);
        startTimeButton = (Button) findViewById(R.id.add_event_start_time);
        endDateButton = (Button) findViewById(R.id.add_event_end_date);
        endTimeButton = (Button) findViewById(R.id.add_event_end_time);
        descriptionText = (EditText) findViewById(R.id.add_event_description);
        addEventButton = (Button) findViewById(R.id.add_event_button);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddEvent().execute();
            }
        });

        setOnClickDatePicker(startDateButton);
        setOnClickDatePicker(endDateButton);
        setOnClickTimePickerEditor(startTimeButton);
        setOnClickTimePickerEditor(endTimeButton);
    }

    public void setOnClickDatePicker(final Button editDate) {
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddStateChart.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        editDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });
    }


    public void setOnClickTimePickerEditor(final Button timeButton) {
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddStateChart.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHourString = selectedHour + "";
                        if (selectedHourString.length() == 1) {
                            selectedHourString = "0" + selectedHourString;
                        }
                        String selectedMinuteString = selectedMinute + "";
                        if (selectedMinuteString.length() == 1) {
                            selectedMinuteString = "0" + selectedMinuteString;
                        }
                        timeButton.setText(selectedHourString + ":" + selectedMinuteString);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    private class AddEvent extends AsyncTask<Void,Void,Void> {
        private ProgressDialog pd;
        private String startDate;
        private String endDate;
        private String startTime;
        private  String endTime;
        private  String title;
        private  String description;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddStateChart.this);
            pd.setMessage("banter");
            pd.show();
            startDate = startDateButton.getText().toString();
            endDate = endDateButton.getText().toString();
            startTime = startTimeButton.getText().toString();
            endTime = endTimeButton.getText().toString();
            title = titleET.getText().toString();
            description = descriptionText.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Firebase fb = ref.child(ref.getAuth().getUid()).child("stateChart").push();
            Map<String,String> map = new HashMap<>();
            map.put("title",title);
            map.put("startDate",startDate);
            map.put("endDate",endDate);
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("description", description);

            fb.setValue(map);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            onBackPressed();
            finish();
        }
    }
}