package com.armdri.myracle;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestAPITask extends AsyncTask<String, Void, JSONObject> {
    // Variable to store url
    protected String mURL;

    // Constructor
    public RestAPITask(String url) {
        mURL = url;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject resJson = null;

        try {
            // Open the connection
            URL url = new URL(mURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();

            // Get the stream
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Set the result
            resJson = new JSONObject(builder.toString());
        }
        catch (Exception e) {
            // Error calling the rest api
            Log.e("REST_API", "GET method failed: " + e.getMessage());
            e.printStackTrace();
        }

        return resJson;
    }
}

