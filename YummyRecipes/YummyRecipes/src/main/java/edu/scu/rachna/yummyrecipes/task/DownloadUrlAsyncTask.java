package edu.scu.rachna.yummyrecipes.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Rachna on 3/10/2016.
 */
public class DownloadUrlAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private Context context;
    private PowerManager.WakeLock wakeLock;

    public DownloadUrlAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlString = params[0];
        URL url = null;
        HttpsURLConnection urlConnection = null;
        try {
            url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            Bitmap bm = BitmapFactory.decodeStream(inputStream);
            return bm;
        } catch (java.io.IOException e) {
            Toast.makeText(context, "Error occured while fetching image from backendless.", Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    protected void onCancelled() {
        wakeLock.release();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        wakeLock.acquire();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        wakeLock.release();
    }

}
