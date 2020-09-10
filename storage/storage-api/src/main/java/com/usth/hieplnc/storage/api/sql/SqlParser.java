package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The interface define action of tabular data parser
 * 
 */

import java.io.IOException;

import org.json.simple.JSONObject;

public interface SqlParser{
    public void save(String path, String name, JSONObject schema, JSONObject data) throws IOException;
    public JSONObject load(String path) throws IOException;
}