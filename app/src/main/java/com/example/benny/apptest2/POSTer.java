package com.example.benny.apptest2;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Benny on 3/22/2018.
 */

public abstract class POSTer extends AsyncTask<RequestData, Void, JSONObject> {
    public String log = "log:\n";
    public boolean done = false;
    public String rawData = "";
    public JSONObject response = null;
    RequestData request = null;

    public final boolean jsonGood() {
        return response != null;
    }


    @Override
    protected JSONObject doInBackground(RequestData... data) {
        if(data.length == 1) {
            request = data[0];

            String url = data[0].url;
            String message = data[0].message;

            JSONObject parsed = null;

            try {
                URL reg = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) reg.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setDoInput(true);
                log = log + "Created a urlConnection\n";

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(message);
                writer.flush();
                writer.close();
                out.close();
                log = log + "Closed everything writer\n";

                connection.connect();
                log = log + "Connected\n";

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder serverResponse = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    serverResponse.append(line);
                }

                log = log + "Done Reading response\n";

                try {
                    rawData = serverResponse.toString();
                    parsed = new JSONObject(rawData);
                    response = parsed;
                } catch (Exception ex) {
                    parsed = null;
                    log = log + "json failed to parse\n";
                }

                log = log + "Done Parsing JSON\n";

                connection.disconnect();

                log = log + "All Done\n";
            } catch (Exception ex) {
                log = log + "connection failed";
            }

            onFinish();

            return parsed;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        response = result;
        done = true;
    }

    protected abstract void onFinish();
}