package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The interface define action of tabular data parser
 * 
 */

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlTable;

public interface SqlParser{
    public void save(String path, String name, JSONObject schema, JSONObject data);
    public SqlTable load(String path);
}