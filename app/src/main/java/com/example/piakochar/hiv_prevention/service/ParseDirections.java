package com.example.piakochar.hiv_prevention.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PiaKochar on 9/5/15.
 */
public class ParseDirections {
    private String overview_polyline;
    private String[] distance;
    private String[] duration;
    private String[] instructions;

    public ParseDirections(JSONObject object, JSONParserCallback callback) {
        try {
            JSONObject routes = object.getJSONArray("routes").getJSONObject(0);
            overview_polyline = routes.getJSONObject("overview_polyline").optString("points");
            JSONArray steps = routes.getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            distance = new String[steps.length()];
            duration = new String[steps.length()];
            instructions = new String[steps.length()];
            for (int i = 0; i < steps.length(); i++) {
                JSONObject s = steps.getJSONObject(i);
                distance[i] = s.getJSONObject("distance").optString("text");
                duration[i] = s.getJSONObject("duration").optString("text");
                instructions[i] = s.optString("html_instructions");
            }

        } catch (JSONException e) {
            callback.serviceFailure(e);
        }
    }

    public String getOverview_polyline() {
        System.out.println("OVERVIEW POLYLINE" + overview_polyline.toString());
        return overview_polyline;
    }

    public String[] getDistance() {
        for (int i = 0; i < distance.length; i++) {
            System.out.println("DISTANCE ARRAY " + distance[i]);
        }
        return distance;
    }

    public String[] getDuration() {
        for (int i = 0; i < duration.length; i++) {
            System.out.println("DURATION ARRAY " + duration[i]);
        }
        return duration;
    }

    public String[] getInstructions() {
        for (int i = 0; i < instructions.length; i++) {
            System.out.println("INSTRUCTIONS ARRAY " + instructions[i]);
        }
        return instructions;
    }
}
