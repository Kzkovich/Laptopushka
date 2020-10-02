package ru.kzkovich.laptopushka.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.kzkovich.laptopushka.services.DownloadTask;
import ru.kzkovich.laptopushka.models.LaptopCharacteristics;
import ru.kzkovich.laptopushka.services.PriceListParcer;
import ru.kzkovich.laptopushka.R;

import static ru.kzkovich.laptopushka.utils.Constants.SETTINGS_PASSWORD;
import static ru.kzkovich.laptopushka.utils.Constants.URL_TO_DOWNLOAD_FILE;

public class MainActivity extends AppCompatActivity
        implements PasswordDialog.PasswordDialogListener {

    private List<String> localVideoFiles = new ArrayList<>();
    FragmentManager manager;
    PasswordDialog passwordDialog;
    VideoView mVideoView;
    Button mDownloadButton;
    Button mPlayButton;
    File localFile;
    private static int currentVideo = 0;
    ProgressBar mProgressBar;
    ImageButton mMenuButton;
    TextView mChrBrand;
    TextView mChrModel;
    TextView mChrCPU;
    TextView mChrScreen;
    TextView mChrRAM;
    TextView mChrHDD;
    TextView mChrGraphics;
    TextView mChrResolution;
    TextView mChrMatrix;
    TextView mChrPrice;
    static int retryPassword = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        findAllViews();
        manager = getSupportFragmentManager();
        mMenuButton.setOnClickListener(viewClickListener);
        mProgressBar.setIndeterminate(false);
        localVideoFiles.clear();

        localFile = new File(getFilesDir().getAbsolutePath() + "/prices.xlsx");
    }

    private void findAllViews() {
        mVideoView = findViewById(R.id.videoView);
        mDownloadButton = findViewById(R.id.downloadFile);
        mPlayButton = findViewById(R.id.playVideo);
        mProgressBar = findViewById(R.id.progressBar);
        mChrBrand = findViewById(R.id.chrBrand);
        mChrModel = findViewById(R.id.chrModel);
        mChrCPU = findViewById(R.id.chrCPU);
        mChrScreen = findViewById(R.id.chrScreen);
        mChrRAM = findViewById(R.id.chrRAM);
        mChrHDD = findViewById(R.id.chrHDD);
        mChrGraphics = findViewById(R.id.chrGraphics);
        mChrResolution = findViewById(R.id.chrResolution);
        mChrMatrix = findViewById(R.id.chrMatrix);
        mChrPrice = findViewById(R.id.chrPrice);
        mMenuButton = findViewById(R.id.menu_imagebutton);
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.enterPassword:
                    Toast.makeText(MainActivity.this, "Secret", Toast.LENGTH_SHORT).show();
                    passwordDialog = new PasswordDialog();
                    passwordDialog.show(manager, "passwordDialog");
                default:
                    return true;
            }
        });
        popupMenu.show();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super .onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void downloadPrice(View view) {
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this, mProgressBar);
        downloadTask.execute(URL_TO_DOWNLOAD_FILE, localFile.getAbsolutePath());
    }

    public void playVideo(View view) {
        Uri video = Uri.parse(localVideoFiles.get(0));
        mVideoView.setVideoURI(video);
        mVideoView.start();
        mVideoView.setOnCompletionListener(mp -> {
            if (!(currentVideo < localVideoFiles.size())) {
                return;
            }
            Uri nextUri = Uri.parse(localVideoFiles.get(currentVideo++));
            mVideoView.setVideoURI(nextUri);
            mVideoView.start();
            if (currentVideo == localVideoFiles.size()) {
                currentVideo = 0;
            }
        });
    }

    public void populateCharacteristics(View view) {
        final PriceListParcer priceListParcer = new PriceListParcer("N1019410", localFile.getAbsolutePath());
        LaptopCharacteristics characteristics = priceListParcer.getCharacteristicsObject();
        mChrBrand.setText(characteristics.getBrand());
        mChrModel.setText(characteristics.getModel());
        mChrCPU.setText(characteristics.getCpu());
        mChrScreen.setText(characteristics.getScreen());
        mChrRAM.setText(characteristics.getRam());
        mChrHDD.setText(characteristics.getHdd());
        mChrGraphics.setText(characteristics.getGraphics());
        mChrResolution.setText(characteristics.getResolution());
        mChrMatrix.setText(characteristics.getMatrixType());
        mChrPrice.setText(characteristics.getPriceInDollars().toString());
        Log.d("LaptopCharacteristic", characteristics.toString());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        ++retryPassword;
        EditText passwordET = dialog.getDialog().findViewById(R.id.passwordEditText);
        String password = passwordET.getText().toString();
        if (isPasswordOk(password)) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else {
            Toast.makeText(this, "Пароль не тот", Toast.LENGTH_SHORT).show();
            passwordDialog = new PasswordDialog();
            passwordDialog.show(manager, "Retry password attempt #" + retryPassword);

        }

    }

    private boolean isPasswordOk(String password) {
        return password.equals(SETTINGS_PASSWORD);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(this, "Ну как хошь", Toast.LENGTH_SHORT).show();
    }
}