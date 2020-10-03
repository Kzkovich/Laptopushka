package ru.kzkovich.laptopushka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import ru.kzkovich.laptopushka.R;

public class SettingsActivity extends AppCompatActivity {
    Button cancelButton;
    Button saveButton;
    TextInputLayout articulET;
    TextInputLayout rateET;
    String articul;
    Double rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        articulET = findViewById(R.id.textInputArticul);
        rateET = findViewById(R.id.textInputRate);
    }

    public void cancelAndGoBack(View view) {
        finish();
    }

    public void saveAndApply(View view) {
        articul = articulET.getEditText().getText().toString();
        rate = Double.parseDouble(rateET.getEditText().getText().toString());

        if (validatedArticulField() || validateRateField()) {
            this.finish();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("articul", articul);
            intent.putExtra("rate", rate);
            startActivity(intent);
        }
    }

    private boolean validatedArticulField() {

        String articul = articulET.getEditText().getText().toString().trim();

        if (articul.isEmpty()) {
            articulET.setError("Введи артикул");
            return false;
        } else {
            articulET.setError("");
            return true;
        }
    }

    private boolean validateRateField() {

        String rate = rateET.getEditText().getText().toString().trim();

        if (rate.isEmpty() || rate.equals("0")) {
            rateET.setError("Введи артикул");
            return false;
        } else {
            rateET.setError("");
            return true;
        }
    }


}