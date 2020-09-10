package com.usth.hieplnc.storage.hadoop.sql;

/**
 * DOC:
 * - In memory file parser
 *
 */

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.*;

public class VirtualFileParser implements SqlParser{
// variable
    private JSONObject schema = null;
    private JSONObject data = null;

//=================================================================//
// constructor

//=================================================================//
// method
    @Override
    public void save(String path, String name, JSONObject schema, JSONObject data) throws IOException{
        this.schema = schema;
        this.data = data;
        if(name != null){
            this.schema.replace("name", name);
        }
    }

    @Override
    public JSONObject load(String path) throws IOException{
        JSONObject dataFrame = new JSONObject();
        // build name
        dataFrame.put("name", schema.get("name"));
        
        // build fields
        List<String> cloneFields = new ArrayList<String>();
        cloneFields.addAll((List<String>) this.schema.get("fields"));
        dataFrame.put("fields", cloneFields);

        // build type
        List<Integer> cloneType = new ArrayList<Integer>();
        List<Integer> type = (List<Integer>) this.schema.get("type");
        for(int value: type){
            cloneType.add(value);
        }
        dataFrame.put("type", cloneType);

        // build data
        List<List<String>> cloneData = new ArrayList<List<String>>();
        List<List<String>> data = (List<List<String>>) this.data.get("data");
        for(List<String> row: data){
            List<String> cloneRow = new ArrayList<String>();
            cloneRow.addAll(row);
            cloneData.add(cloneRow);
        }
        dataFrame.put("data", cloneData);

        return dataFrame;
    }

}