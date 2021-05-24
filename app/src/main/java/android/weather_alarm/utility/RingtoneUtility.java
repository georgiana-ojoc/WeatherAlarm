package android.weather_alarm.utility;

import android.weather_alarm.R;

public class RingtoneUtility {
    public static int getRingtoneResource(String ringtone) {
        if (ringtone.equals("Aura tone")) {
            return R.raw.aura_tone;
        }
        if (ringtone.equals("Day break")) {
            return R.raw.day_break;
        }
        if (ringtone.equals("Early riser")) {
            return R.raw.early_riser;
        }
        if (ringtone.equals("Fresh start")) {
            return R.raw.fresh_start;
        }
        if (ringtone.equals("Happy day")) {
            return R.raw.happy_day;
        }
        if (ringtone.equals("Morning birds")) {
            return R.raw.morning_birds;
        }
        if (ringtone.equals("Ping")) {
            return R.raw.ping;
        }
        if (ringtone.equals("Slow morning")) {
            return R.raw.slow_morning;
        }
        if (ringtone.equals("Soft chime")) {
            return R.raw.soft_chime;
        }
        return -1;
    }

    public static int getRingtonePosition(String ringtone) {
        if (ringtone.equals("Aura tone")) {
            return 0;
        }
        if (ringtone.equals("Day break")) {
            return 1;
        }
        if (ringtone.equals("Early riser")) {
            return 2;
        }
        if (ringtone.equals("Fresh start")) {
            return 3;
        }
        if (ringtone.equals("Happy day")) {
            return 4;
        }
        if (ringtone.equals("Morning birds")) {
            return 5;
        }
        if (ringtone.equals("Ping")) {
            return 6;
        }
        if (ringtone.equals("Slow morning")) {
            return 7;
        }
        if (ringtone.equals("Soft chime")) {
            return 8;
        }
        return 0;
    }
}
