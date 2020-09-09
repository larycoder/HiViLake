package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The interface represents the result table
 * 
 */

import org.json.simple.JSONObject;

public interface SqlResult{
    public JSONObject getSchema();
    public JSONObject getData();
}