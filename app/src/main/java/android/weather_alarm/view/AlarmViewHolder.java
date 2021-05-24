package android.weather_alarm.view;

import android.view.View;
import android.weather_alarm.R;
import android.weather_alarm.data.Alarm;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private final TextView timeTextView;
    private final TextView nameTextView;
    private final TextView recurringDaysTextView;
    private final SwitchMaterial scheduledSwitch;
    private final OnCheckedChangedListener listener;

    public AlarmViewHolder(View itemView, OnCheckedChangedListener listener) {
        super(itemView);
        timeTextView = itemView.findViewById(R.id.timeTextView);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        recurringDaysTextView = itemView.findViewById(R.id.recurringDaysTextView);
        scheduledSwitch = itemView.findViewById(R.id.scheduledSwitch);
        this.listener = listener;
    }

    public void bind(Alarm alarm) {
        timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", alarm.getHour(),
                alarm.getMinute()));
        nameTextView.setText(alarm.getName());
        recurringDaysTextView.setText(alarm.computeRecurringDays());
        scheduledSwitch.setChecked(alarm.isScheduled());
        setOnCheckedChangeListener(((buttonView, isChecked) -> listener.onCheckedChanged(alarm)));
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        scheduledSwitch.setOnCheckedChangeListener(listener);
    }
}
