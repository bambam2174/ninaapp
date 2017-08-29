package de.sedatkilinc.ninaapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by sedat on 8/29/17.
 */

public class HTTPGet extends AsyncTask<String, Void, String> {

    String server_response;
    @Override
    protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection connection = null;

        try {
            url = new URL(strings[0]);


        } catch (MalformedURLException mex) {
            Log.d("MalformedURLException", mex.getLocalizedMessage());
            Log.d("MalformedURLException", mex.getMessage());
            mex.printStackTrace();
        } catch (IOException iex) {
            Log.d("IOException", iex.getLocalizedMessage());
            Log.d("IOException", iex.getMessage());
            iex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.e("Response", "" + server_response);

    }
}
