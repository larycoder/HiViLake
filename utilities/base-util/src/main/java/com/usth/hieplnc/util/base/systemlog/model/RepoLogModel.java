package com.usth.hieplnc.util.base.systemlog.model;

/**
 * DOC:
 * - Model of repository log
 *
 */

import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.model.*;

public class RepoLogModel{
// variable
    public String name = "";
    public String describe = "";
    public String type = "";
    public float size = 0;
    public long qualityFile = 0;
    public long totalFile = 0;
    public String timeLocation = "";
    public long updateTime = 0;
    public long auditTime = 0;
    public String status = "";
    public String notes = "";

//=================================================================//
// constructor
    public RepoLogModel(){ // factory constructor
        ;
    }

    public RepoLogModel(JSONObject data){
        this.name = (String) data.get("name");
        this.describe = (String) data.get("describe");
        this.type = (String) data.get("type");
        this.size = (float) data.get("float");
        this.qualityFile = (long) data.get("qualityFile");
        this.totalFile = (long) data.get("totalFile");
        this.timeLocation = (String) data.get("timeLocation");
        this.updateTime = (long) data.get("updateTime");
        this.auditTime = (long) data.get("auditTime");
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
        fields.add("describe");
        fields.add("type");
        fields.add("size");
        fields.add("qualityFile");
        fields.add("totalFile");
        fields.add("timeLocation");
        fields.add("updateTime");
        fields.add("auditTime");
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
        row.add(this.describe);
        row.add(this.type);
        row.add(Float.toString(this.size));
        row.add(Long.toString(this.qualityFile));
        row.add(Long.toString(this.totalFile));
        row.add(timeLocation);
        row.add(Long.toString(this.updateTime));
        row.add(Long.toString(this.auditTime));
        row.add(status);
        row.add(notes);
        data.add(row);
        dataFrame.put("data", data);

        return dataFrame;
    }
}