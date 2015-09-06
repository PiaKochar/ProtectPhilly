package com.example.piakochar.hiv_prevention.service;

import org.json.JSONArray;

/**
 * Created by PiaKochar on 9/5/15.
 */
public interface CondomsServiceCallback {
    void serviceSuccess(JSONArray properties_list);
    void serviceFailure(Exception e);
}
