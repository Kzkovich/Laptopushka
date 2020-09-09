package ru.kzkovich.laptopushka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private List<String> localVideoFiles = new ArrayList<>();
    VideoView videoView;
    Button downloadButton;
    Button playButton;
    private static int currentVideo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        videoView = findViewById(R.id.videoView);
        downloadButton = findViewById(R.id.loadVideo);
        playButton = findViewById(R.id.playVideo);
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

    public void downloadAndRunVideo(View view) {
        listAllVideosFromStorage();
    }

    private void listAllVideosFromStorage() {
        final String localFileStorage = MainActivity.this.getApplicationInfo().dataDir;
        final File[] localFileList = new File(localFileStorage).listFiles();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference videosRef = storage.getReference().child("videos");
        videosRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference fileStorage : listResult.getItems()) {

                }
            }
        });
        videosRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Toast.makeText(MainActivity.this, "Загружаю файлов " + listResult.getItems().size(), Toast.LENGTH_SHORT).show();
                        for (final StorageReference referenceToDownload : listResult.getItems()) {

                            if (localFileList == null) {
                                downloadFile(localFileStorage, referenceToDownload);
                            } else {
                                for (File localFile : localFileList) {
                                    if (referenceToDownload.getName().equals(localFile.getName())) {
                                        //TODO здесь какая-то хуйня, должны скипать такие файлы, придумать
                                    }
                                }
                                downloadFile(localFileStorage, referenceToDownload);
                            }




                        }
                    }

                    private void downloadFile(String localFileStorage, final StorageReference referenceToDownload) {
                        final File whereToDownload = new File(localFileStorage + File.separatorChar + referenceToDownload.getName());
                        referenceToDownload.getFile(whereToDownload).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(MainActivity.this, "Файл " + referenceToDownload.getName() + " загружен", Toast.LENGTH_SHORT).show();
                                localVideoFiles.add(whereToDownload.getAbsolutePath());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Упс! При загрузке возникла ошибка " + e, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                Log.d("FileDownload", "Загружено - " + referenceToDownload.getName() + " - " + snapshot.getBytesTransferred() + " байт");
                            }
                        });
                    }

                    private void checkBeforeDownload(StorageReference items) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Упс! При получении файлов из хранилища возникла ошибка " + e, Toast.LENGTH_LONG).show();
                    }
                });
        downloadButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "Все файлы загружены, жмякай \"Запуск плеера\" ", Toast.LENGTH_SHORT).show();
    }

    public void playVideo(View view) {
        playButton.setVisibility(View.GONE);
        Uri video = new Uri(localVideoFiles.get(0));
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