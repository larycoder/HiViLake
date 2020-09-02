package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * Hadoop Storage class implementing the storage wrapper
 *
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// Storage wrapper interface
import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.api.filesystem.*;
import com.usth.hieplnc.storage.api.filesystem.model.*;

/**
 * WRANNING:
 * - Missing Exception throwing in close method
 *
 */

public class Storage implements FilesystemWrapper{
// variable
    
    private final Configuration conf;
    private final FileSystem fs;

//=================================================================//
// constructor

    public Storage(Configuration conf) throws IOException{
        this.conf = conf;
        this.fs = (new Path("hdfs://")).getFileSystem(this.conf);
    }

//=================================================================//
// method
    @Override
    public void setParam(String key, String value){
        this.conf.set(key, value);
    }

    @Override
    public List<String> support(){
        List supportType = new ArrayList<String>();
        supportType.add("filesystem");
        supportType.add("sql");
        return supportType;
    }

    @Override
    public void close(){
        try{
            fs.close();
        } catch(IOException e){
            ;
        }
    }

    @Override
    public void createPath(String path, PathType type){
        ;
    }
    
    @Override
    public void deletePath(String path, SWOption option){
        ;
    }
    
    @Override
    public SWFile openFile(String path){
        return null;
    }
    
    @Override
    public SWFilestatus getStatus(String path){
        return null;
    }
    
    @Override
    public Iterator<SWFilestatus> searchFile(JSONObject pattern){
        return null;
    }

    public static void main(String args[]) throws IOException{
// test StorageWrapper
        Configuration hadoopConf = new Configuration();
        hadoopConf.set("fs.defaultFS", "hdfs://localhost:9000");
        StorageWrapper myStore = (StorageWrapper) new Storage(hadoopConf);
// type of storage supported by this class
        for(String type: myStore.support()){
            System.out.println(type);
        }
        myStore.close();
//=====================================================================//
    }
}
