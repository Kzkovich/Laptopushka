package ru.kzkovich.laptopushka.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.kzkovich.laptopushka.R;
import ru.kzkovich.laptopushka.models.CharacteristicsConfig;
import ru.kzkovich.laptopushka.models.LaptopCharacteristics;
import ru.kzkovich.laptopushka.services.DownloadTask;
import ru.kzkovich.laptopushka.services.PriceListParcer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static ru.kzkovich.laptopushka.utils.Constants.SETTINGS_PASSWORD;
import static ru.kzkovich.laptopushka.utils.Constants.URL_TO_DOWNLOAD_FILE;

public class MainActivity extends AppCompatActivity
        implements PasswordDialog.PasswordDialogListener {

    private List<File> localVideoFiles = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 100;
    FragmentManager manager;
    LaptopCharacteristics characteristics;
    CharacteristicsConfig config;
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
        Intent intent = getIntent();
        config = new CharacteristicsConfig(intent.getStringExtra("articul"), intent.getDoubleExtra("rate", 0));
        findAllViews();
        manager = getSupportFragmentManager();
        mMenuButton.setOnClickListener(viewClickListener);
        mProgressBar.setIndeterminate(false);
        localVideoFiles.clear();
        playVideo();
        localFile = new File(getFilesDir().getAbsolutePath() + "/prices.xlsx");

        downloadPrice();
        populateCharacteristics();
    }

    private List<File> populateVideoPaths(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files;

        requestPermission();

        if (checkPermission()) {
            files = parentDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".mp4")) {
                        if (!inFiles.contains(file)) inFiles.add(file);
                    }
                }
            }
        } else {
            requestPermission();
        }
        return inFiles;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Need permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "granted");
            } else {
                Log.e("value", "denied");
            }
            break;
        }
    }


    private void findAllViews() {
        mVideoView = findViewById(R.id.videoView);
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

    View.OnClickListener viewClickListener = v -> showPopupMenu(v);

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

    public void downloadPrice() {
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this, mProgressBar);
        downloadTask.execute(URL_TO_DOWNLOAD_FILE, localFile.getAbsolutePath());
    }

    public void playVideo() {
        if (localVideoFiles.size() == 0) {
            localVideoFiles = populateVideoPaths(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
            playVideo();
        } else {
            Uri video = Uri.parse(localVideoFiles.get(0).getAbsolutePath());
            mVideoView.setVideoURI(video);
            mVideoView.start();
            mVideoView.setOnCompletionListener(mp -> {
                if (!(currentVideo < localVideoFiles.size())) {
                    return;
                }
                Uri nextUri = Uri.parse(localVideoFiles.get(currentVideo++).getAbsolutePath());
                mVideoView.setVideoURI(nextUri);
                mVideoView.start();
                if (currentVideo == localVideoFiles.size()) {
                    currentVideo = 0;
                }
            });
        }
    }

    public void populateCharacteristics() {
        final PriceListParcer priceListParcer = new PriceListParcer(config, localFile.getAbsolutePath());
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
        mChrPrice.setText(characteristics.getPriceInUAH().toString());
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

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(this, "Ну как хошь", Toast.LENGTH_SHORT).show();
    }

    private boolean isPasswordOk(String password) {
        return password.equals(SETTINGS_PASSWORD);
    }

}