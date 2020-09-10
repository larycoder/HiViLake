package com.usth.hieplnc.storage.api.sql;

/**
 * Doc:
 * - The interface provides sql wrapper for data store
 * 
 */

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.api.sql.SqlParser;
import com.usth.hieplnc.storage.api.sql.SqlTable;
import com.usth.hieplnc.storage.api.sql.SqlResult;

public interface SqlWrapper extends StorageWrapper{
    public List<JSONObject> listParser();
    public SqlParser getParser(int index);
    public SqlTable use(String path, JSONObject extra);
    public SqlTable use(SqlResult table);
    public void createTable(String path, String name, JSONObject schema, SqlParser parser) throws IOException;
    public void addTable(String path, String name, SqlResult table, SqlParser parser) throws IOException;
    public void dropTable(String path) throws IOException;
}