package android.weather_alarm.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.weather_alarm.AlarmFields;
import android.weather_alarm.R;
import android.weather_alarm.activity.StartAlarmActivity;
import android.weather_alarm.utility.RingtoneUtility;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.weather_alarm.AlarmApplication.CHANNEL_ID;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private int ringtoneIndex;

    @Override
    public void onCreate() {
        super.onCreate();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent startAlarmIntent = new Intent(this, StartAlarmActivity.class);

        int id = intent.getIntExtra(AlarmFields.ID, -1);
        String name = intent.getStringExtra(AlarmFields.NAME);
        String ringtone = intent.getStringExtra(AlarmFields.RINGTONE);

        startAlarmIntent.putExtra(AlarmFields.ID, id);
        startAlarmIntent.putExtra(AlarmFields.NAME, name);
        startAlarmIntent.putExtra(AlarmFields.RINGTONE, ringtone);

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Activity.ACTIVITY_SERVICE);
        String packageName = activityManager.getRunningTasks(1).get(0).topActivity
                .getPackageName();

        if (packageName.equals("project.weather_alarm")) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle(String.format("\"%s\" alarm", name))
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
            startForeground(1, notification);

            startAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startAlarmIntent);
        } else {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    startAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle(String.format("\"%s\" alarm", name))
                    .setContentText("Touch to snooze or stop.")
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .build();
            startForeground(1, notification);
        }

        ringtoneIndex = RingtoneUtility.getRingtoneResource(ringtone);
        if (ringtoneIndex != -1) {
            mediaPlayer = MediaPlayer.create(this, ringtoneIndex);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        }

        long[] pattern = {0, 100, 1000};
        vibrator.vibrate(pattern, 0);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (ringtoneIndex != -1) {
            mediaPlayer.stop();
        }

        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}