package com.example.piakochar.hiv_prevention.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PiaKochar on 9/5/15.
 */
public class JSONParser {
    private JSONArray properties_list;
    private String[] siteName;
//    private String[] hours;
    private String[] addr;
    private Double[][] coordinates;
    private JSONParserCallback callback;


    public JSONParser(String s, JSONParserCallback callback, boolean clinics) {
        this.callback = callback;
        if (!clinics) {
            condomSites(s);
        } else {
            clinicSites(s);
        }
    }

    public String[] getSiteName() {
        return siteName;
    }
//    public String[] getHours() {
//        return hours;
//    }

    public String[] getAddr() {
        return addr;
    }

    public Double[][] getCoordinates() {
        return coordinates;
    }

    public void condomSites(String s) {
        try {
            JSONObject data = new JSONObject(s);
            properties_list = data.getJSONArray("features");
            siteName = new String[properties_list.length()];
//            hours = new String[properties_list.length()];
            addr = new String[properties_list.length()];
            coordinates = new Double[properties_list.length()][2];


            for (int i = 0; i < properties_list.length(); i++) {
                JSONObject feature = properties_list.getJSONObject(i);
                siteName[i] = feature.getJSONObject("properties").optString("SITE_NAME");
//                hours[i] = feature.getJSONObject("properties").optString("HOURS");
                addr[i] = feature.getJSONObject("properties").optString("ADDRESS");

                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coords = geometry.optJSONArray("coordinates");

                for (int j = 0; j < coords.length(); j++) {
                    double x = coords.optDouble(j);
                    coordinates[i][j] = x;
                }
            }

        } catch (Exception e) {
            callback.serviceFailure(e);
        }
    }

    public void clinicSites(String s) {
        try {
            JSONObject data = new JSONObject(s);
            properties_list = data.getJSONArray("features");
            siteName = new String[properties_list.length()];
//            hours = new String[properties_list.length()];
            addr = new String[properties_list.length()];
            coordinates = new Double[properties_list.length()][2];


            for (int i = 0; i < properties_list.length(); i++) {
                JSONObject feature = properties_list.getJSONObject(i);
                siteName[i] = feature.getJSONObject("properties").optString("NAME");
//                hours[i] = feature.getJSONObject("properties").optString("HOURS");
                addr[i] = feature.getJSONObject("properties").optString("FULL_ADDRESS");

                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coords = geometry.optJSONArray("coordinates");

                for (int j = 0; j < coords.length(); j++) {
                    double x = coords.optDouble(j);
                    coordinates[i][j] = x;
                }
            }

        } catch (Exception e) {
            callback.serviceFailure(e);
        }
    }



}
