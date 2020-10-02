package ru.kzkovich.laptopushka.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.kzkovich.laptopushka.dao.CharacteristicsConfigDAO;
import ru.kzkovich.laptopushka.dao.LaptopCharacteristicsDAO;
import ru.kzkovich.laptopushka.database.AppDatabase;
import ru.kzkovich.laptopushka.models.CharacteristicsConfig;
import ru.kzkovich.laptopushka.models.LaptopCharacteristics;

public class CharacteristicsRepository {

    private LaptopCharacteristicsDAO characteristicsDAO;
    private CharacteristicsConfigDAO configDAO;
    private LiveData<List<LaptopCharacteristics>> allCharacteristics;
    private LiveData<List<CharacteristicsConfig>> allConfigs;

    public CharacteristicsRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        characteristicsDAO = db.characteristicsDAO();
        configDAO = db.characteristicsConfigDAO();
        allCharacteristics = characteristicsDAO.loadCharacteristics();
        allConfigs = configDAO.loadConfig();
    }

    public LiveData<List<LaptopCharacteristics>> getAllCharacteristics() {
        return allCharacteristics;
    }

    public LiveData<List<CharacteristicsConfig>> getAllConfigs() {
        return allConfigs;
    }

    public void insert(LaptopCharacteristics characteristics) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                characteristicsDAO.insertCharacteristics(characteristics));
    }

    public void insert(CharacteristicsConfig config) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                configDAO.insertConfig(config));
    }

    public void update(LaptopCharacteristics characteristics) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                characteristicsDAO.updateCharacteristics(characteristics));
    }

    public void update(CharacteristicsConfig config) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                configDAO.updateConfig(config));
    }
}
