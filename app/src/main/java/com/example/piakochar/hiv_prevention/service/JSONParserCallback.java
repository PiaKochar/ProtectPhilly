package com.example.piakochar.hiv_prevention.service;

import org.json.JSONArray;

/**
 * Created by PiaKochar on 9/5/15.
 */
public interface JSONParserCallback {
    void serviceSuccess();
    void serviceFailure(Exception e);
}

