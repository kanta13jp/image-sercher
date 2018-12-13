package com.example.kanta.myapplication.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpAsyncLoader extends AsyncTaskLoader<String> {

    private URL url = null; // WebAPIのURL

    public HttpAsyncLoader(Context context, String url) {
        super(context);
        Log.e(this.getClass().getSimpleName(),"HttpAsyncLoader() url = " + url);
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(),e.getMessage());
        }
    }

    @Override
    public String loadInBackground() {
        Log.e(this.getClass().getSimpleName(),"loadInBackground()");
        HttpURLConnection urlConnection = null;
        String responseBody = "";
        try {
            urlConnection = (HttpURLConnection) this.url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                System.out.print(current);
                responseBody += String.valueOf(current);
            }

            System.out.print(responseBody);
            Log.e(this.getClass().getSimpleName(),"loadInBackground() responseBody = " + responseBody);
            return responseBody;
        }
        catch (Exception e) {
            Log.e(this.getClass().getSimpleName(),e.getMessage());
        }
        finally {
            // 通信終了時は、接続を閉じる
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}