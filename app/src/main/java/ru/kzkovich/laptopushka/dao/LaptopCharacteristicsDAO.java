package ru.kzkovich.laptopushka.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.kzkovich.laptopushka.models.LaptopCharacteristics;

@Dao
public interface LaptopCharacteristicsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCharacteristics(LaptopCharacteristics characteristics);

    @Update
    public void updateCharacteristics(LaptopCharacteristics characteristics);

    @Delete
    public void deleteCharacteristics(LaptopCharacteristics characteristics);

    @Query("SELECT * FROM laptop_characteristics")
    public List<LaptopCharacteristics> loadCharacteristics();

    @Query("SELECT * FROM laptop_characteristics WHERE articul LIKE :articul")
    public List<LaptopCharacteristics> findCharacteristicsWithArticul(String articul);
}
