package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * Hadoop StorageFactory
 * The class used to build storage instance
 *
 */

import org.apache.hadoop.conf.Configuration;

// google simple json lib
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;
import java.io.IOException;

// Storage Wrapper interface
import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.hadoop.Storage;

public class StorageFactory{
// variable
    private Configuration conf;

//=================================================================//
// constructor
    public StorageFactory(){
        this.conf = new Configuration();
    }

//=================================================================//
// method
    public StorageFactory setParam(String key, String value){
        this.conf.set(key, value);
        return this;
    }

    public StorageFactory setParam(JSONObject conf){
        Set<String> keys = conf.keySet();
        
        for(String key: keys){
            this.conf.set(key, (String) conf.get(key));
        }

        return this;
    }

    public JSONObject getParam(){
        JSONObject jsonParam = new JSONObject();
        jsonParam.putAll((Map)this.conf);
        return jsonParam;
    }

    public StorageWrapper newInstance() throws IOException{
        return (StorageWrapper) new Storage(this.conf);
    }
}