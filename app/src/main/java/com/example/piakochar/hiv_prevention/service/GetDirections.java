package com.example.piakochar.hiv_prevention.service;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by PiaKochar on 9/5/15.
 */
public class GetDirections {
    private Exception error;
    private ParseDirections parse;
    private AsyncTask<String, Void, String> task;

    public GetDirections(String endpoint, final JSONParserCallback callback) {

        task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                System.out.println("PARAMS" + params.toString());
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
//
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("THIS IS THE RESULT:" + result.toString());

                    System.out.println("RESULT");

                    return result.toString();

                } catch (Exception e) {
                   callback.serviceFailure(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
//                System.out.println("IN POST EXECUTE: " + s);
                if (s == null && error != null ){
                    callback.serviceFailure(error);
                }
                 try {
                     JSONObject data = new JSONObject(s);
                     System.out.println("JSON OBJECT: " + data.toString());
                     parse = new ParseDirections(data, callback);
                     System.out.println("PARSING DIRECTIONS");
                     parse.getDistance();
                     parse.getDuration();
                     parse.getInstructions();
                     callback.serviceSuccess();

                 } catch (Exception e) {
                     callback.serviceFailure(e);
                }
            }
        }.execute(endpoint);
    }

    public String getOverviewPolyline()  {
        return parse.getOverview_polyline();
    }

    public String[] getDistance() {
        return parse.getDistance();
    }

    public String[] getDuration() {
        return parse.getDuration();
    }

    public String[] getInstructions() {
        return parse.getInstructions();
    }

    public AsyncTask<String, Void, String> getTask() {
        return task;
    }
}

