package com.usth.hieplnc.storage.hadoop;

/**
 * DOC:
 * - the implementation class for SqlResult api of Hivilake
 *
 */

import java.util.List;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.*;

public class SqlResultWrapper implements SqlResult{
// variable
    private JSONObject schema;
    private JSONObject data;

//=================================================================//
// constructor
    public SqlResultWrapper(String tableName, List<String> fields, List<Integer> type, List<List<String>> data){
        this.schema = new JSONObject();
        this.schema.put("name", tableName);
        this.schema.put("fields", fields);
        this.schema.put("type", type);

        this.data = new JSONObject();
        this.data.put("data", data);
    }

//=================================================================//
// method
    @Override
    public JSONObject getSchema(){
        return this.schema;
    }

    @Override
    public JSONObject getData(){
        return this.data;
    }

}