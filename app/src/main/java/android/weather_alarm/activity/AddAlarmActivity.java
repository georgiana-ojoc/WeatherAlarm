package android.weather_alarm.activity;

import android.os.Bundle;
import android.weather_alarm.R;
import android.weather_alarm.data.Alarm;
import android.weather_alarm.utility.AnimationUtility;
import android.weather_alarm.utility.TimePickerUtility;
import android.weather_alarm.view.AlarmViewModel;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dpro.widgets.WeekdaysPicker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class AddAlarmActivity extends AppCompatActivity {
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        TimePicker timePicker = findViewById(R.id.timePicker);
        EditText setNameEditText = findViewById(R.id.setNameEditText);

        WeekdaysPicker weekDaysPicker = findViewById(R.id.weekDaysPicker);
        LinkedHashMap<Integer, Boolean> setDays = new LinkedHashMap<>();
        setDays.put(MONDAY, false);
        setDays.put(TUESDAY, false);
        setDays.put(WEDNESDAY, false);
        setDays.put(THURSDAY, false);
        setDays.put(FRIDAY, false);
        setDays.put(SATURDAY, false);
        setDays.put(SUNDAY, false);
        weekDaysPicker.setCustomDays(setDays);

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

            Alarm alarm = new Alarm(new Random().nextInt(Integer.MAX_VALUE),
                    TimePickerUtility.geHour(timePicker),
                    TimePickerUtility.getMinute(timePicker),
                    recurring, TimePickerUtility.isSelectedDay(selectedDays, MONDAY),
                    TimePickerUtility.isSelectedDay(selectedDays, TUESDAY),
                    TimePickerUtility.isSelectedDay(selectedDays, WEDNESDAY),
                    TimePickerUtility.isSelectedDay(selectedDays, THURSDAY),
                    TimePickerUtility.isSelectedDay(selectedDays, FRIDAY),
                    TimePickerUtility.isSelectedDay(selectedDays, SATURDAY),
                    TimePickerUtility.isSelectedDay(selectedDays, SUNDAY), name);
            alarm.schedule(getApplicationContext(), false);

            alarmViewModel.insert(alarm);

            finish();
        });
    }
}
