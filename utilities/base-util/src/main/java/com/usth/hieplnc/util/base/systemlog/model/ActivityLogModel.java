package com.usth.hieplnc.util.base.systemlog.model;

/**
 * DOC:
 * - Model of activity
 *
 */

import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.model.*;

public class ActivityLogModel{
// variable
    public String name = "";
    public String action = "";
    public JSONObject parameter = new JSONObject();
    public long time = 0;
    public JSONObject response =  new JSONObject();
    public String status = "";
    public String notes = "";

//=================================================================//
// constructor
    public ActivityLogModel(){ // factory constructor
        ;
    }

    public ActivityLogModel(JSONObject data){
        this.name = (String) data.get("name");
        this.action = (String) data.get("action");
        this.parameter = (JSONObject) data.get("parameter");
        this.time = (long) data.get("time");
        this.response = (JSONObject) data.get("response");
        this.status = (String) data.get("status");
        this.notes = (String) data.get("notes");
    }

//=================================================================//
// method
    public JSONObject toDataFrame(String name){
        JSONObject dataFrame = new JSONObject();
        // build name
        dataFrame.put("name", name);

        // build fields
        List<String> fields = new ArrayList<String>();
        fields.add("name");
        fields.add("action");
        fields.add("parameter");
        fields.add("time");
        fields.add("response");
        fields.add("status");
        fields.add("notes");
        dataFrame.put("fields", fields);

        // build type
        List<Integer> type = new ArrayList<Integer>();
        for(int i = 0; i < fields.size(); i++){
            type.add(DataType.STRING);
        }
        dataFrame.put("type", type);

        // build data
        List<List<String>> data = new ArrayList<List<String>>();
        List<String> row = new ArrayList<String>();
        row.add(this.name);
        row.add(this.action);
        row.add(this.parameter.toString());
        row.add(Long.toString(this.time));
        row.add(this.response.toString());
        row.add(this.status);
        row.add(this.notes);
        data.add(row);
        dataFrame.put("data", data);

        return dataFrame;
    }
}