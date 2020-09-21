package com.usth.hieplnc.util.base.systemlog.model;

/**
 * DOC:
 * - Model of user
 *
 */

import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.model.*;

public class CentralInfoModel{
// variable
    public String name = "";
    public String desc = "";

//=================================================================//
// constructor
    public CentralInfoModel(){ // factory constructor
        ;
    }

    public CentralInfoModel(JSONObject data){
        this.name = (String) data.get("name");
        this.desc = (String) data.get("describe");
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
        row.add(this.desc);
        data.add(row);
        dataFrame.put("data", data);

        return dataFrame;
    }
}