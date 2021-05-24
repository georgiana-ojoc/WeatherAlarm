package android.weather_alarm.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weather_alarm.R;
import android.weather_alarm.data.Alarm;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private List<Alarm> alarms;
    private final OnCheckedChangedListener listener;

    public AlarmRecyclerViewAdapter(OnCheckedChangedListener listener) {
        this.alarms = new ArrayList<>();
        this.listener = listener;
    }

    public Alarm getAlarm(int id) {
        if (id >= 0 && id < getItemCount()) {
            return alarms.get(id);
        }
        return null;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent,
                false);
        return new AlarmViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.bind(alarm);
    }

    @Override
    public void onViewRecycled(@NonNull AlarmViewHolder holder) {
        super.onViewRecycled(holder);
        holder.setOnCheckedChangeListener(null);
    }
}
