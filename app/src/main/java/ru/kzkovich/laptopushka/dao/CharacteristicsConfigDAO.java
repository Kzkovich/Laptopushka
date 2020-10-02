package ru.kzkovich.laptopushka.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.kzkovich.laptopushka.models.CharacteristicsConfig;

@Dao
public interface CharacteristicsConfigDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertConfig(CharacteristicsConfig config);

    @Update
    public void updateConfig(CharacteristicsConfig config);

    @Delete
    public void deleteConfig(CharacteristicsConfig config);

    @Query("SELECT * FROM config")
    public List<CharacteristicsConfig> loadConfig();

    @Query("SELECT * FROM config WHERE articul LIKE :articul")
    public List<CharacteristicsConfig> loadConfigWithArticul(String articul);
}
