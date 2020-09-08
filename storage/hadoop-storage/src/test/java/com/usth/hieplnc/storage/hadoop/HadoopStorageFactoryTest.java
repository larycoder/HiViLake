package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * The class provide junit test for StorageFactory
 *
 */

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.StorageWrapper;

public class HadoopStorageFactoryTest{
    @Test
    public void setupStorageFactoryParamByKey(){
        // setup
        StorageFactory storeFact = new StorageFactory();
        // exec
        storeFact.setParam("fs.defaultFS", "hdfs://localhost:9000").setParam("dfs.replication", "1");
        JSONObject storeParam = storeFact.getParam();
        // assert
        assertEquals("test set parameter and get parameter from StorageFactory", storeParam.get("fs.defaultFS"), "hdfs://localhost:9000");
        assertEquals("test set parameter and get parameter from StorageFactory", storeParam.get("dfs.replication"), "1");

    }

    @Test
    public void setupStorageFactoryParamByJson(){
        // setup
        StorageFactory storeFact = new StorageFactory();
        JSONObject param = new JSONObject();
        JSONObject storeParam = new JSONObject();

        // exec
        param.put("fs.defaultsFS", "hdfs://localhost:9000");
        param.put("dfs.replication", "1");
        storeFact.setParam(param);
        storeParam = storeFact.getParam();

        // assert
        assertEquals("test setup Param in StoreFactory", storeParam.get("fs.defaultsFS"), "hdfs://localhost:9000");
        assertEquals("test setup Param in StoreFactory", storeParam.get("dfs.replication"), "1");
    }

    @Test
    public void createNewStorageWrapper(){
        // setup
        StorageFactory storeFact = new StorageFactory();
        // exec
        storeFact.setParam("fs.defaultFS", "hdfs://localhost:9000");
        StorageWrapper store = null;
        try{
            store = storeFact.newInstance();
        } catch(IOException e){
            e.printStackTrace();
        }
        // assert
        assertTrue("test create StorageWrapper from StorageFactory", store instanceof Storage);

        // close storage
        try{
            store.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}