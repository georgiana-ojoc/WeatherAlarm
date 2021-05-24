package android.weather_alarm.view;

import android.app.Application;
import android.weather_alarm.data.Alarm;
import android.weather_alarm.data.AlarmRepository;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AlarmViewModel extends AndroidViewModel {
    private final AlarmRepository alarmRepository;
    private final LiveData<List<Alarm>> alarms;

    public AlarmViewModel(Application application) {
        super(application);
        alarmRepository = new AlarmRepository(application);
        alarms = alarmRepository.get();
    }

    public LiveData<List<Alarm>> get() {
        return alarms;
    }

    public LiveData<Alarm> get(int id) {
        return alarmRepository.get(id);
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }

    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }

    public void delete(int id) {
        alarmRepository.delete(id);
    }
}
