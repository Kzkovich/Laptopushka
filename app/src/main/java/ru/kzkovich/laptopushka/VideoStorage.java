package ru.kzkovich.laptopushka;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class VideoStorage {
    private URI videoUri;

    VideoStorage(URI videoUri) {
        this.videoUri = videoUri;
    }

    public ArrayList<Video> getAllVideos() {
        ArrayList<Video> videoInStorage = new ArrayList<>();
        if (!this.isEmpty()) {
            //List Videos in local storage
        }
        return videoInStorage;
    }

    boolean isEmpty() {
        if (videoUri.toString().isEmpty()) return true;
        File videoFolder = new File(videoUri);
        return videoFolder.listFiles().length > 0;
    }
}
