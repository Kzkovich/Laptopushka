package ru.kzkovich.laptopushka.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.kzkovich.laptopushka.R;

public class SettingsActivity extends AppCompatActivity {
    Button cancelButton;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
    }

    public void cancelAndGoBack(View view) {
        finish();
    }
}