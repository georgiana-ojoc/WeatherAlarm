package android.weather_alarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.weather_alarm.AlarmFields;
import android.weather_alarm.R;
import android.weather_alarm.data.Alarm;
import android.weather_alarm.view.AlarmRecyclerViewAdapter;
import android.weather_alarm.view.AlarmViewModel;
import android.weather_alarm.view.OnCheckedChangedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements OnCheckedChangedListener {
    private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(this);
        RecyclerView alarmsRecyclerView = findViewById(R.id.alarmsRecyclerView);
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmViewModel.get().observe(this, alarms -> {
            if (alarms != null) {
                alarmRecyclerViewAdapter.setAlarms(alarms);
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Alarm alarm = alarmRecyclerViewAdapter.getAlarm(viewHolder.getAdapterPosition());
                if (alarm != null) {
                    if (direction == ItemTouchHelper.RIGHT) {
                        Intent modifyAlarmActivity = new Intent(MainActivity.this,
                                ModifyAlarmActivity.class);
                        modifyAlarmActivity.putExtra(AlarmFields.ID, alarm.getId());
                        modifyAlarmActivity.putExtra(AlarmFields.HOUR, alarm.getHour());
                        modifyAlarmActivity.putExtra(AlarmFields.MINUTE, alarm.getMinute());
                        modifyAlarmActivity.putExtra(AlarmFields.MONDAY, alarm.isMonday());
                        modifyAlarmActivity.putExtra(AlarmFields.TUESDAY, alarm.isTuesday());
                        modifyAlarmActivity.putExtra(AlarmFields.WEDNESDAY, alarm.isWednesday());
                        modifyAlarmActivity.putExtra(AlarmFields.THURSDAY, alarm.isThursday());
                        modifyAlarmActivity.putExtra(AlarmFields.FRIDAY, alarm.isFriday());
                        modifyAlarmActivity.putExtra(AlarmFields.SATURDAY, alarm.isSaturday());
                        modifyAlarmActivity.putExtra(AlarmFields.SUNDAY, alarm.isSunday());
                        modifyAlarmActivity.putExtra(AlarmFields.NAME, alarm.getName());
                        modifyAlarmActivity.putExtra(AlarmFields.RINGTONE, alarm.getRingtone());
                        startActivity(modifyAlarmActivity);
                    } else {
                        alarm.cancel(MainActivity.this);
                        alarmViewModel.delete(alarm.getId());
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(alarmsRecyclerView);

        FloatingActionButton addAlarmButton = findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(view -> {
            Intent addAlarmActivity = new Intent(MainActivity.this,
                    AddAlarmActivity.class);
            startActivity(addAlarmActivity);
        });
    }

    @Override
    public void onCheckedChanged(Alarm alarm) {
        if (alarm.isScheduled()) {
            alarm.cancel(this);
        } else {
            alarm.schedule(this, true);
        }
        alarmViewModel.update(alarm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmRecyclerViewAdapter.notifyDataSetChanged();
    }
}