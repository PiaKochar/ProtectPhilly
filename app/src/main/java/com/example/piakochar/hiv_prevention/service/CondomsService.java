package com.example.piakochar.hiv_prevention.service;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

/**
 * Created by PiaKochar on 9/5/15.
 */
public class CondomsService {
    private CondomsServiceCallback callback;
    private Exception error;
    private JSONArray properties_list;

    public CondomsService(CondomsServiceCallback callback) {
        this.callback = callback;
    }

    public void getData() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
//                try {
//
//                    AssetManager assetManager = getAssets();
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(getAssets().open("GIS_HEALTH.Condom_distribution_sites.json")));
//
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//                } catch (Exception e) {
//                    System.out.println("Exception in CondomsService - doInBackground");
//                    error = e;
//                }

                return null;
//                String endpoint = "http://data.phl.opendata.arcgis.com/datasets/85732a1de65741c0aadac960ef7f1ea3_0.geojson";
//
//                try {
//                    URL url = new URL(endpoint);
//                    URLConnection connection = url.openConnection();
//
//                    InputStream inputStream = connection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                    StringBuilder result = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//
//                    }
//                } catch (Exception e) {
//                    error = e;
//                }
//
//                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                // finished doInBackground, but got an exception
                if (s == null && error != null) {
                    callback.serviceFailure(error);
                    return;
                }
                try {
                    System.out.println("String to JSON: " + s);
                    JSONObject data = new JSONObject(s);
                    properties_list = data.getJSONArray("features");

                    callback.serviceSuccess(properties_list);

                } catch (JSONException e) {
                    callback.serviceFailure(e);
                }
            }


        }.execute("temp");

    }
}