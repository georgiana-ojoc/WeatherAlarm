package android.weather_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.weather_alarm.service.AlarmService;
import android.weather_alarm.service.RescheduleAlarmsService;
import android.widget.Toast;

import java.util.Calendar;


public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(AlarmFields.ID, -1);
        String name = intent.getStringExtra(AlarmFields.NAME);
        String ringtone = intent.getStringExtra(AlarmFields.RINGTONE);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String text = "Alarm received after boot completed";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        } else {
            String text = "Alarm received";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            if (!intent.getBooleanExtra(AlarmFields.RECURRING, false)) {
                startAlarmService(context, id, name, ringtone);
            } else {
                if (isScheduledToday(intent)) {
                    startAlarmService(context, id, name, ringtone);
                }
            }
        }
    }

    private void startAlarmService(Context context, int id, String name, String ringtone) {
        Intent intentService = new Intent(context, AlarmService.class);

        intentService.putExtra(AlarmFields.ID, id);
        intentService.putExtra(AlarmFields.NAME, name);
        intentService.putExtra(AlarmFields.RINGTONE, ringtone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private boolean isScheduledToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        switch (today) {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(AlarmFields.MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(AlarmFields.TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(AlarmFields.WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(AlarmFields.THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(AlarmFields.FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(AlarmFields.SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(AlarmFields.SUNDAY, false);
            default:
                return false;
        }
    }
}