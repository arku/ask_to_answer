package com.example.arun.asktoanswer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lazyass on 9/7/15.
 */
public class Constants {
    public final static String URL = "http://gmkmcglanzendo.in/";

    public String getResponseFromServer(String url){
        java.net.URL link = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream;
        BufferedReader bufferedReader = null;
        String line;
        StringBuffer buffer = new StringBuffer();

        try {
            link = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.v("AsyncTask", "malformed");
        }

        try {
            //Open the connection

            httpURLConnection = (HttpURLConnection) link.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();


            Log.v("AsyncTask", "after get request");

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.v("onPostExecute","request success");
                inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


                // read from the urlconnection via the bufferedreader


                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                bufferedReader.close();
                String response = buffer.toString().trim();
                //Log.v("AsyncTask", "adding");

                Log.v("AsyncTask", response);

                return response;
            }

        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);

        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }

        }
        return "No response from server";
    }
}
