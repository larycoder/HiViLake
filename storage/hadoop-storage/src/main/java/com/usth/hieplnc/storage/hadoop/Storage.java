package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * Hadoop Storage class implementing the storage wrapper
 *
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// Storage wrapper interface
import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.api.Filesystem;

public class Storage implements StorageWrapper{
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

    public static void main(String args[]) throws IOException{
// test StorageWrapper
        Configuration hadoopConf = new Configuration();
        hadoopConf.set("fs.defaultFS", "hdfs://localhost:9000");
        StorageWrapper myStore = new Storage(hadoopConf);
// type of storage supported by this class
        for(String type: myStore.support()){
            System.out.println(type);
        }
        myStore.close();
//=====================================================================//
    }
}
