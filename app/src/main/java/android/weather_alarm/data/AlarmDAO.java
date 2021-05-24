package android.weather_alarm.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDAO {
    @Query("SELECT * FROM alarm_table ORDER BY hour, minute")
    LiveData<List<Alarm>> get();

    @Query("SELECT * FROM alarm_table WHERE id = :id")
    LiveData<Alarm> get(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Query("DELETE FROM alarm_table WHERE id = :id")
    void delete(int id);
}
