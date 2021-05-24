package android.weather_alarm.utility;

import android.os.Build;
import android.widget.TimePicker;

import java.util.List;

public final class TimePickerUtility {
    public static int geHour(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getHour();
        } else {
            return timePicker.getCurrentHour();
        }
    }

    public static int getMinute(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getMinute();
        } else {
            return timePicker.getCurrentMinute();
        }
    }

    public static void setHour(TimePicker timePicker, int hour) {
        if (hour != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(hour);
            } else {
                timePicker.setCurrentHour(hour);
            }
        }
    }

    public static void setMinute(TimePicker timePicker, int minute) {
        if (minute != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setMinute(minute);
            } else {
                timePicker.setCurrentMinute(minute);
            }
        }
    }

    public static boolean isSelectedDay(List<Integer> selectedDays, int day) {
        return selectedDays.contains(day);
    }
}
