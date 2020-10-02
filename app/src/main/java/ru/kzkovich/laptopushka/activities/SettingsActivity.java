package ru.kzkovich.laptopushka.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ru.kzkovich.laptopushka.R;
import ru.kzkovich.laptopushka.models.CharacteristicsConfig;
import ru.kzkovich.laptopushka.repository.CharacteristicsRepository;

public class SettingsActivity extends AppCompatActivity {
    Button cancelButton;
    Button saveButton;
    EditText articulET;
    EditText rateET;
    String articul;
    Double rate;
    CharacteristicsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        articulET = findViewById(R.id.articulET);
        rateET = findViewById(R.id.rateET);
    }

    public void cancelAndGoBack(View view) {
        finish();
    }

    public void saveAndApply(View view) {
        articul = articulET.getText().toString();
        rate = Double.parseDouble(rateET.getText().toString());
        CharacteristicsConfig config = new CharacteristicsConfig(articul, rate);
        AsyncTask.execute(() -> {
            repository = new CharacteristicsRepository(getApplication());
            repository.insert(config);
        });
    }
}