package android.weather_alarm.service;

import android.content.Intent;
import android.os.IBinder;
import android.weather_alarm.data.Alarm;
import android.weather_alarm.data.AlarmRepository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

public class RescheduleAlarmsService extends LifecycleService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AlarmRepository alarmRepository = new AlarmRepository(getApplication());
        alarmRepository.get().observe(this, alarms -> {
            if (alarms != null) {
                for (Alarm alarm : alarms) {
                    if (alarm.isScheduled()) {
                        alarm.schedule(getApplicationContext(), false);
                    }
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return null;
    }
}
