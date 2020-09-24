package ru.kzkovich.laptopushka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import static android.app.DownloadManager.*;
import static android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE;
import static android.app.DownloadManager.EXTRA_DOWNLOAD_ID;

public class MainActivity extends AppCompatActivity {

    private List<String> localVideoFiles = new ArrayList<>();
    VideoView videoView;
    Button downloadButton;
    Button playButton;
    DownloadManager dm;
    String URL_TO_DOWNLOAD_PRICE = "http://1619149.po369583.web.hosting-test.net/price/Gazik_laptops_web.xlsx";
    private static int currentVideo = 0;
    private long enque;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        videoView = findViewById(R.id.videoView);
        downloadButton = findViewById(R.id.downloadFile);
        playButton = findViewById(R.id.playVideo);
        mProgressBar = new ProgressBar(MainActivity.this);
        mProgressBar.setId(R.id.progressBar);
        mProgressBar.setIndeterminate(true);
        localVideoFiles.clear();
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
        File localFile = new File(Environment.getDataDirectory() + "/prices.xlsx");
        localFile.mkdir();
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this, view);
        downloadTask.execute(URL_TO_DOWNLOAD_PRICE);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTask.cancel(true);
            }
        });
    }



    public static String getDataDir(Context context) throws Exception {
        return context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .applicationInfo.dataDir;
    }

    public void playVideo(View view) {
        playButton.setVisibility(View.GONE);
        Uri video = Uri.parse(localVideoFiles.get(0));
        videoView.setVideoURI(video);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!(currentVideo < localVideoFiles.size())) {
                    return;
                }
                Uri nextUri = Uri.parse(localVideoFiles.get(currentVideo++));
                videoView.setVideoURI(nextUri);
                videoView.start();
                if (currentVideo == localVideoFiles.size()) {
                    currentVideo = 0;
                }
            }
        });
    }

}