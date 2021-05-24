package android.weather_alarm.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AlarmRepository {
    private final AlarmDAO alarmDAO;
    private final LiveData<List<Alarm>> alarms;

    public AlarmRepository(Application application) {
        AlarmDatabase database = AlarmDatabase.getDatabase(application);
        alarmDAO = database.alarmDAO();
        alarms = alarmDAO.get();
    }

    public LiveData<List<Alarm>> get() {
        return alarms;
    }

    public LiveData<Alarm> get(int id) {
        return alarmDAO.get(id);
    }

    public void insert(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> alarmDAO.insert(alarm));
    }

    public void update(Alarm alarm) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> alarmDAO.update(alarm));
    }

    public void delete(int id) {
        AlarmDatabase.databaseWriteExecutor.execute(() -> alarmDAO.delete(id));
    }
}
