package android.weather_alarm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.weather_alarm.AlarmFields;
import android.weather_alarm.R;
import android.weather_alarm.utility.AnimationUtility;
import android.weather_alarm.utility.RingtoneUtility;
import android.weather_alarm.utility.TimePickerUtility;
import android.weather_alarm.view.AlarmViewModel;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dpro.widgets.WeekdaysPicker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class ModifyAlarmActivity extends AppCompatActivity {
    private AlarmViewModel alarmViewModel;
    private String ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_alarm_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        Intent intent = getIntent();

        TimePicker timePicker = findViewById(R.id.timePicker);
        TimePickerUtility.setHour(timePicker, intent.getIntExtra(AlarmFields.HOUR, -1));
        TimePickerUtility.setMinute(timePicker, intent.getIntExtra(AlarmFields.MINUTE, -1));

        EditText setNameEditText = findViewById(R.id.enterNameEditText);
        setNameEditText.setText(intent.getStringExtra(AlarmFields.NAME));

        WeekdaysPicker weekDaysPicker = findViewById(R.id.weekDaysPicker);
        LinkedHashMap<Integer, Boolean> setDays = new LinkedHashMap<>();
        setDays.put(MONDAY, intent.getBooleanExtra(AlarmFields.MONDAY, false));
        setDays.put(TUESDAY, intent.getBooleanExtra(AlarmFields.TUESDAY, false));
        setDays.put(WEDNESDAY, intent.getBooleanExtra(AlarmFields.WEDNESDAY, false));
        setDays.put(THURSDAY, intent.getBooleanExtra(AlarmFields.THURSDAY, false));
        setDays.put(FRIDAY, intent.getBooleanExtra(AlarmFields.FRIDAY, false));
        setDays.put(SATURDAY, intent.getBooleanExtra(AlarmFields.SATURDAY, false));
        setDays.put(SUNDAY, intent.getBooleanExtra(AlarmFields.SUNDAY, false));
        weekDaysPicker.setCustomDays(setDays);

        Spinner spinner = findViewById(R.id.ringtonesSpinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.ringtones, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        String oldRingtone = intent.getStringExtra(AlarmFields.RINGTONE);
        spinner.setSelection(RingtoneUtility.getRingtonePosition(oldRingtone));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ringtone = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ringtone = oldRingtone;
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            AnimationUtility.animateButtonTextColor(cancelButton);
            finish();
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            AnimationUtility.animateButtonTextColor(saveButton);

            boolean recurring = true;
            if (weekDaysPicker.noDaySelected()) {
                recurring = false;
            }
            List<Integer> selectedDays = weekDaysPicker.getSelectedDays();
            String name = setNameEditText.getText().toString();
            if (name.isEmpty()) {
                name = "Alarm";
            }

            int id = intent.getIntExtra(AlarmFields.ID, -1);
            if (id != -1) {
                AtomicBoolean observed = new AtomicBoolean(false);
                boolean finalRecurring = recurring;
                String finalName = name;
                Context context = getApplicationContext();
                alarmViewModel.get(id).observe(this, alarm -> {
                    if (!observed.get() && alarm != null) {
                        alarm.cancel(context);

                        alarm.setHour(TimePickerUtility.geHour(timePicker));
                        alarm.setMinute(TimePickerUtility.getMinute(timePicker));
                        alarm.setRecurring(finalRecurring);
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, MONDAY));
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, TUESDAY));
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, WEDNESDAY));
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, THURSDAY));
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, FRIDAY));
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, SATURDAY));
                        alarm.setMonday(TimePickerUtility.isSelectedDay(selectedDays, SUNDAY));
                        alarm.setName(finalName);
                        alarm.setRingtone(ringtone);
                        alarm.schedule(getApplicationContext(), true);

                        alarmViewModel.update(alarm);

                        observed.set(true);
                    }
                });
            }

            finish();
        });
    }
}
