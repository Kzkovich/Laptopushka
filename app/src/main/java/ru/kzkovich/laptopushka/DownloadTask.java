package ru.kzkovich.laptopushka;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class DownloadTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private PowerManager.WakeLock mWhakeLock;
    private File localFile;
    private URL urlToDownload;
    private ReadableByteChannel readableByteChannel;
    private FileOutputStream fileOutputStream;
    private FileChannel fileChannel;
    private String priceFileName;
    private View view;
    private ProgressBar mProgressBar;

    DownloadTask (Context context, View view) {
        this.context = context;
        this.view = view;
        mProgressBar = view.findViewById(R.id.progressBar);
        this.priceFileName = "/prices.xlsx";
        this.urlToDownload = null;
        this.readableByteChannel = null;
        this.fileOutputStream = null;
        this.localFile = localFile;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            //checking for HTTP 200
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Сервер вернул HTTP " +
                        connection.getResponseCode() +
                        " " +
                        connection.getResponseMessage();
            }

            // getting a length of a content
            int fileLength = connection.getContentLength();

            //download
            input = connection.getInputStream();
            //saving
            output = new FileOutputStream(context.getDataDir().getAbsolutePath() + priceFileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                //if it was cancelled from mProgressBar
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;

                //getting length to use in progressbar
                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, "Exception " + e, Toast.LENGTH_SHORT).show();
        } finally {
            try {
                //close Input and Output Streams
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Toast.makeText(context, "IOException " + ioe, Toast.LENGTH_SHORT).show();
            }
            //close Connection
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //locking the CPU not to go offline if power button pressed
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWhakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWhakeLock.acquire();
//        mProgressBar.setVisibility(View.VISIBLE);
        Toast.makeText(context, "Файл загружается", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
//        mProgressBar.setIndeterminate(false);
//        mProgressBar.setMax(100);
//        mProgressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mWhakeLock.release();
//        mProgressBar.setVisibility(View.GONE);
        if (s != null) {
            Toast.makeText(context, "Ошибка загрузки: " + s, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Прайс был успешно загружен!", Toast.LENGTH_SHORT).show();
        }
    }
}