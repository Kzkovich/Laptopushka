package ru.kzkovich.laptopushka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.DownloadManager.*;

public class MainActivity extends AppCompatActivity {

    private List<String> localVideoFiles = new ArrayList<>();
    private long enque;
    VideoView videoView;
    Button downloadButton;
    Button playButton;
    private static int currentVideo = 0;
    DownloadManager dm;
    String URL_TO_DOWNLOAD_PRICE = "http://1619149.po369583.web.hosting-test.net/price/Gazik_laptops_web.xlsx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        videoView = findViewById(R.id.videoView);
        downloadButton = findViewById(R.id.downloadFile);
        playButton = findViewById(R.id.playVideo);
        registerReceiver(receiver, new IntentFilter(ACTION_DOWNLOAD_COMPLETE));
        localVideoFiles.clear();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(EXTRA_DOWNLOAD_ID, 0);
                Query query = new Query();
                query.setFilterById(enque);
                Cursor c = dm.query(query);

                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(COLUMN_STATUS);

                    if (STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        Toast.makeText(MainActivity.this, "Загрузка завершена", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

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
        File localFile = new File(Environment.getDataDirectory().getAbsoluteFile() + Resources.getSystem().getString(R.string.local_file_name));
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Request request = new Request(Uri.parse(URL_TO_DOWNLOAD_PRICE)).setDestinationUri(Uri.fromFile(localFile));
        enque = dm.enqueue(request);

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