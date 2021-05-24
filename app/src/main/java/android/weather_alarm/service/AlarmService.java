package android.weather_alarm.service;

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

    @Override
    public void onCreate() {
        super.onCreate();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, StartAlarmActivity.class);

        int id = intent.getIntExtra(AlarmFields.ID, -1);
        String name = intent.getStringExtra(AlarmFields.NAME);
        String ringtone = intent.getStringExtra(AlarmFields.RINGTONE);

        notificationIntent.putExtra(AlarmFields.ID, id);
        notificationIntent.putExtra(AlarmFields.NAME, name);
        notificationIntent.putExtra(AlarmFields.RINGTONE, ringtone);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(String.format("\"%s\" alarm", name))
                .setContentText("Touch to snooze or stop.")
                .setContentIntent(pendingIntent)
                .build();

        int ringtoneIndex = RingtoneUtility.getRingtoneResource(ringtone);
        if (ringtoneIndex != -1) {
            mediaPlayer = MediaPlayer.create(this, ringtoneIndex);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        long[] pattern = {0, 100, 1000};
        vibrator.vibrate(pattern, 0);

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}