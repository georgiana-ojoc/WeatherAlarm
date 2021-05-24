package android.weather_alarm.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.weather_alarm.AlarmFields;
import android.weather_alarm.R;
import android.weather_alarm.data.Alarm;
import android.weather_alarm.service.AlarmService;
import android.weather_alarm.view.AlarmViewModel;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartAlarmActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_alarm_activity);

        AlarmViewModel alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        Intent intent = getIntent();
        int id = intent.getIntExtra(AlarmFields.ID, -1);
        String name = intent.getStringExtra(AlarmFields.NAME);
        String ringtone = intent.getStringExtra(AlarmFields.RINGTONE);

        if (ringtone.equals("Weather forecast")) {
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    if (textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech
                            .LANG_COUNTRY_AVAILABLE) {
                        textToSpeech.setLanguage(Locale.US);
                        textToSpeech.speak("Hello! The weather is nice.",
                                TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        Toast.makeText(StartAlarmActivity.this,
                                "US Text to speech not available", Toast.LENGTH_LONG)
                                .show();
                    }
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(StartAlarmActivity.this,
                            "Text to speech not working", Toast.LENGTH_LONG).show();
                }
            });
        }

        TextClock textClock = findViewById(R.id.textClock);
        ObjectAnimator clockAnimator = ObjectAnimator.ofFloat(textClock, "alpha",
                0.25f, 0.5f, 0.75f, 1f, 0.75f, 0.5f, 0.25f);
        clockAnimator.setDuration(1000);
        clockAnimator.setRepeatCount(ValueAnimator.INFINITE);

        TextView showNameTextView = findViewById(R.id.showNameTextView);
        showNameTextView.setText(name);
        ObjectAnimator nameAnimator = ObjectAnimator.ofFloat(showNameTextView, "alpha",
                1f, 0.75f, 0.5f, 0.25f, 0.5f, 0.75f, 1.f);
        nameAnimator.setDuration(1000);
        nameAnimator.setRepeatCount(ValueAnimator.INFINITE);

        clockAnimator.start();
        nameAnimator.start();

        Context context = getApplicationContext();

        Button snoozeButton = findViewById(R.id.snoozeButton);
        snoozeButton.setOnClickListener(view -> {
            if (id != -1) {
                AtomicBoolean observed = new AtomicBoolean(false);
                alarmViewModel.get(id).observe(this, alarm -> {
                    if (!observed.get() && alarm != null) {
                        alarm.cancel(context);
                        alarmViewModel.update(alarm);
                        observed.set(true);
                    }
                });
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.MINUTE, 10);

            Alarm alarm = new Alarm(new Random().nextInt(Integer.MAX_VALUE),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    false, false, false, false, false,
                    false, false, false, name + " snooze", ringtone);
            alarm.schedule(context, true);

            stopService(context);
        });

        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(view -> {
            if (id != -1) {
                AtomicBoolean observed = new AtomicBoolean(false);
                alarmViewModel.get(id).observe(this, alarm -> {
                    if (!observed.get() && alarm != null) {
                        alarm.cancel(context);
                        alarmViewModel.update(alarm);
                        observed.set(true);
                    }
                });
            }

            stopService(context);
        });
    }

    private void stopService(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
        finish();
    }
}
