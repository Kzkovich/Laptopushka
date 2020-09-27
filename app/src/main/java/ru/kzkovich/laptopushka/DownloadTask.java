package ru.kzkovich.laptopushka;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, String> {
//    private Context context;
    private PowerManager.WakeLock mWhakeLock;
    private WeakReference<ProgressBar> mProgressBar;
    private WeakReference<Context> context;
    private String localUrl;

    DownloadTask (Context context, ProgressBar progressBar) {
        this.context = new WeakReference<>(context);
        this.mProgressBar = new WeakReference<>(progressBar);
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
            localUrl = sUrl[1];
            output = new FileOutputStream(localUrl);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {

                //if it was cancelled from mProgressBar. It is not using
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
                Toast.makeText(context.get(), "IOException " + ioe, Toast.LENGTH_SHORT).show();
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
        PowerManager pm = (PowerManager) context.get().getSystemService(Context.POWER_SERVICE);
        mWhakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWhakeLock.acquire(10*60*1000L /*10 minutes*/);
        mProgressBar.get().setVisibility(View.VISIBLE);

        //progressbar still indeterminate
        mProgressBar.get().setIndeterminate(false);
        mProgressBar.get().setProgress(0);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d("filePath", "Загружено: " + values[0]);
        //progress still indeterminate mode
        mProgressBar.get().setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //unlocking cpu
        mWhakeLock.release();

        //hiding progressbar
        mProgressBar.get().setVisibility(View.GONE);

        //alert to user of result
        if (s != null) {
            Toast.makeText(context.get(), "Ошибка загрузки: " + s, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context.get(), "Прайс был успешно загружен!", Toast.LENGTH_SHORT).show();
            Log.d("filePath", "Файл загружен: " + localUrl);
        }
    }
}