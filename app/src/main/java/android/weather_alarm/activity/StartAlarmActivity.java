package android.weather_alarm.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartAlarmActivity extends AppCompatActivity implements LocationListener,
        TextToSpeech.OnInitListener {
    private LocationManager locationManager;
    private RequestQueue requestQueue;
    private TextToSpeech textToSpeech;

    private String ringtone;
    private boolean started = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_alarm_activity);

        AlarmViewModel alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel
                .class);

        Intent intent = getIntent();
        int id = intent.getIntExtra(AlarmFields.ID, -1);
        String name = intent.getStringExtra(AlarmFields.NAME);
        ringtone = intent.getStringExtra(AlarmFields.RINGTONE);

        if (ringtone.equals("Weather forecast")) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            requestQueue = Volley.newRequestQueue(this);
            textToSpeech = new TextToSpeech(this, this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission
                            .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission
                            .INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_COARSE_LOCATION, Manifest.permission
                        .ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0, this);
            }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0, this);
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (!started) {
            String key = "";
            String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s" +
                    "&lon=%s&exclude=minutely,daily,alerts&units=metric&appid=%s", latitude,
                    longitude, key);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, response ->
                            speak(getWeatherForecast(response)), error ->
                            speak("I could not get the weather forecast."));

            requestQueue.add(jsonObjectRequest);

            started = true;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech
                    .LANG_COUNTRY_AVAILABLE) {
                textToSpeech.setLanguage(Locale.US);
            } else {
                Toast.makeText(StartAlarmActivity.this,
                        "US Text to speech not available", Toast.LENGTH_SHORT).show();
            }
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(StartAlarmActivity.this,
                    "Text to speech not working", Toast.LENGTH_SHORT).show();
        }
    }

    private String getWeatherForecast(JSONObject response) {
        try {
            JSONObject current = response.getJSONObject("current");
            String currentTemperature = current.getString("temp") + " Celsius degrees";
            String currentFeelsLike = current.getString("feels_like") + " Celsius degrees";
            String currentHumidity = current.getString("humidity") + "%";
            String currentClouds = current.getString("clouds") + "%";
            String currentWindSpeed = current.getString("wind_speed") + " meters per second";
            String currentDescription = current.getJSONArray("weather")
                    .getJSONObject(0).getString("description");

            JSONObject nextHour = response.getJSONArray("hourly").getJSONObject(0);
            String nextHourTemperature = nextHour.getString("temp") + " Celsius degrees";
            String nextHourFeelsLike = nextHour.getString("feels_like") + " Celsius degrees";
            String nextHourHumidity = nextHour.getString("humidity") + "%";
            String nextHourClouds = nextHour.getString("clouds") + "%";
            String nextHourWindSpeed = nextHour.getString("wind_speed") + " meters per " +
                    "second";
            String nextHourDescription = nextHour.getJSONArray("weather")
                    .getJSONObject(0).getString("description");

            return String.format("Hello! A little description of the current weather would be " +
                            "%s. The temperature is around %s and it feels like %s. The humidity " +
                            "is %s, there are %s clouds on the sky and the wind speed is around " +
                            "%s. In the next hour, you will see %s. The temperature will be " +
                            "around %s and it will feel like %s. The humidity will be %s, there " +
                            "will be %s clouds on the sky and the wind speed will be around %s.",
                    currentDescription, currentTemperature, currentFeelsLike, currentHumidity,
                    currentClouds, currentWindSpeed, nextHourDescription, nextHourTemperature,
                    nextHourFeelsLike, nextHourHumidity, nextHourClouds, nextHourWindSpeed);
        } catch (JSONException exception) {
            return "I could not get the weather forecast.";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ringtone.equals("Weather forecast")) {
            textToSpeech.stop();
        }
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void stopService(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
        finish();
    }
}
