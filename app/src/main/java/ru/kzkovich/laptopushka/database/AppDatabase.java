package ru.kzkovich.laptopushka.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.kzkovich.laptopushka.dao.CharacteristicsConfigDAO;
import ru.kzkovich.laptopushka.dao.LaptopCharacteristicsDAO;
import ru.kzkovich.laptopushka.models.CharacteristicsConfig;
import ru.kzkovich.laptopushka.models.LaptopCharacteristics;

@Database(entities = {LaptopCharacteristics.class, CharacteristicsConfig.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LaptopCharacteristicsDAO characteristicsDAO();
    public abstract CharacteristicsConfigDAO characteristicsConfigDAO();

    public static volatile AppDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
