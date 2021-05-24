package android.weather_alarm.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.weather_alarm.AlarmBroadcastReceiver;
import android.weather_alarm.AlarmFields;
import android.weather_alarm.utility.DayUtility;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Locale;

@Entity(tableName = "alarm_table")
public class Alarm {
    @PrimaryKey
    @NonNull
    private Integer id;

    @NonNull
    private Integer hour;

    @NonNull
    private Integer minute;

    private boolean scheduled;

    private boolean recurring;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    private String recurringDays;

    @NonNull
    private String name;

    private String ringtone;

    public Alarm(@NonNull Integer id, @NonNull Integer hour, @NonNull Integer minute,
                 boolean recurring, boolean monday, boolean tuesday, boolean wednesday,
                 boolean thursday, boolean friday, boolean saturday, boolean sunday,
                 @NonNull String name, String ringtone) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.scheduled = false;
        this.recurring = recurring;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.recurringDays = null;
        this.name = name;
        this.ringtone = ringtone;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public Integer getHour() {
        return hour;
    }

    @NonNull
    public Integer getMinute() {
        return minute;
    }


    public boolean isScheduled() {
        return scheduled;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public String getRecurringDays() {
        return recurringDays;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setHour(@NonNull Integer hour) {
        this.hour = hour;
    }

    public void setMinute(@NonNull Integer minute) {
        this.minute = minute;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public void setRecurringDays(String recurringDays) {
        this.recurringDays = recurringDays;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }


    public void schedule(Context context, boolean rescheduled) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(AlarmFields.ID, id);
        intent.putExtra(AlarmFields.RECURRING, recurring);
        intent.putExtra(AlarmFields.MONDAY, monday);
        intent.putExtra(AlarmFields.TUESDAY, tuesday);
        intent.putExtra(AlarmFields.WEDNESDAY, wednesday);
        intent.putExtra(AlarmFields.THURSDAY, thursday);
        intent.putExtra(AlarmFields.FRIDAY, friday);
        intent.putExtra(AlarmFields.SATURDAY, saturday);
        intent.putExtra(AlarmFields.SUNDAY, sunday);
        intent.putExtra(AlarmFields.NAME, name);
        intent.putExtra(AlarmFields.RINGTONE, ringtone);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = getCalendar();

        StringBuilder type = new StringBuilder();
        if (rescheduled) {
            type.append("rescheduled");
        } else {
            type.append("scheduled");
        }
        String text = String.format(Locale.getDefault(), "\"%s\" alarm %s for " +
                "%s at %02d:%02d", name, type.toString(), computeRecurringDays(), hour, minute);
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();

        if (recurring) {
            final long dayMilliSeconds = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    dayMilliSeconds, pendingIntent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
            }
        }

        this.scheduled = true;
    }

    public void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        alarmManager.cancel(pendingIntent);
        this.scheduled = false;
        String text = String.format("\"%s\" alarm canceled", name);
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            int dayOfMonth = Calendar.DAY_OF_MONTH;
            calendar.set(dayOfMonth, calendar.get(dayOfMonth) + 1);
        }
        return calendar;
    }

    public String computeRecurringDays() {
        if (!recurring) {
            return DayUtility.getDay(getCalendar().get(Calendar.DAY_OF_WEEK));
        }
        if (monday && tuesday && wednesday && thursday && friday && saturday && sunday) {
            return "every day";
        }
        if (monday && tuesday && wednesday && thursday && friday && !saturday && !sunday) {
            return "every weekday";
        }
        if (!monday && !tuesday && !wednesday && !thursday && !friday && saturday && sunday) {
            return "every weekend";
        }
        StringBuilder days = new StringBuilder();
        if (monday) {
            days.append("Mon, ");
        }
        if (tuesday) {
            days.append("Tue, ");
        }
        if (wednesday) {
            days.append("Wed, ");
        }
        if (thursday) {
            days.append("Thu, ");
        }
        if (friday) {
            days.append("Fri, ");
        }
        if (saturday) {
            days.append("Sat, ");
        }
        if (sunday) {
            days.append("Sun, ");
        }
        int length = days.length();
        days.delete(length - 2, length);
        return days.toString();
    }
}