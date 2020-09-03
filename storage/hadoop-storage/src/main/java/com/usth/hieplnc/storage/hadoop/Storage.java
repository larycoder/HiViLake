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

import com.usth.hieplnc.storage.hadoop.FileWrapper;
import com.usth.hieplnc.storage.hadoop.FileFinder;

/**
 * WRANNING:
 * - Missing Exception handler in "close" method
 *
 */

public class Storage implements FilesystemWrapper{
// variable
    
    private final Configuration conf;
    private final FileSystem fs;

    private FileFinder searchEngine;

//=================================================================//
// constructor

    public Storage(Configuration conf) throws IOException{
        this.conf = conf;
        this.fs = (new Path("hdfs://")).getFileSystem(this.conf);

        this.searchEngine = new FileFinder(this.fs);
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
    public void close() throws IOException{
        fs.close();
    }

    @Override
    public void createPath(String path, int type) throws IOException{
        Path pathFile = new Path(path);

        if(type == PathType.DIR){
            this.fs.mkdirs(pathFile);
        } else{
            // check file exits
            if(this.fs.exists(pathFile)){
                throw new IOException("File " + path + " is already exsited");
            }

            FSDataOutputStream newFile = this.fs.create(pathFile);
            newFile.hflush();
            newFile.hsync();
            newFile.close();
        }
    }
    
    @Override
    public void deletePath(String path, int option) throws IOException{
        Path delPath = new Path(path);
        
        boolean delOpt = false;
        switch(option){
            case SWOption.ALL:
                delOpt = true;
                break;
            default:
                delOpt = false;
        }

        this.fs.delete(delPath, delOpt);
    }
    
    @Override
    public SWFile openFile(String path) throws IOException{
        return new FileWrapper(this.fs, new Path(path));
    }
    
    @Override
    public SWFilestatus getStatus(String path) throws IOException{
        Path pathFile = new Path(path);

        // check file exits
        if(! this.fs.exists(pathFile)){
            throw new IOException("File " + path + " is not exsited");
        }

        return new FilestatusWrapper(this.fs.getFileStatus(pathFile));
    }

    @Override
    public SWListPath listStatus(SWFilestatus fstatus) throws IOException{
        Path pathFile = new Path(fstatus.getPath());
        return new ListPathWrapper(this.fs.listStatus(pathFile));
    }
    
    @Override
    public Iterator<SWFilestatus> searchFile(JSONObject pattern) throws IOException{
        this.searchEngine.setPattern((String) pattern.get("pattern"));
        return this.searchEngine;
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
