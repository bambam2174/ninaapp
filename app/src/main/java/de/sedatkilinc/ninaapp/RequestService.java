package de.sedatkilinc.ninaapp;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by sedat on 8/21/17.
 */

public class RequestService extends AsyncTask<String, Void, String> {

    private Activity mActivity;
    URL url;

    public RequestService(Activity currentActivity) {
        mActivity = currentActivity;
    }

    private boolean checkInternetPermission() {
        Log.d("INTERNET", String.valueOf(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET)));
        return ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    public String  performPostCall(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String getResponse(String pURL) {
        String result = null;
        try {
            url = new URL(pURL);
            result = this.execute(pURL).get();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("pURL", strings.toString());
        HttpURLConnection connection = null;
        String szResponse = "";
        try {
            connection = (HttpURLConnection)this.url.openConnection();

            //InputStream in = connection.getInputStream();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            //readStream(in);
            Log.d("inputstream", in.toString());
            InputStreamReader isr = new InputStreamReader(in);

            int data = isr.read();
            while (data != -1) {
                char current = (char) data;
                data = isr.read();
                szResponse += current;
                Log.d("char", String.valueOf(current));
            }
            Log.d("Response", szResponse);

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return szResponse;
    }
}
