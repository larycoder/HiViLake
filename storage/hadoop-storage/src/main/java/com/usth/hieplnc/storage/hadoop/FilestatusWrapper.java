package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * FilestatusWrapper represent the status of file in hadoop fs
 *
 */

import org.apache.hadoop.fs.*;

import java.io.IOException;

// Storage wrapper interface
import com.usth.hieplnc.storage.api.filesystem.SWFilestatus;
import com.usth.hieplnc.storage.api.filesystem.SWListPath;

import com.usth.hieplnc.storage.hadoop.ListPathWrapper;

public class FilestatusWrapper implements SWFilestatus{
// variable

    private final FileStatus fstatus;

//=================================================================//
// constructor

    public FilestatusWrapper(FileStatus fstatus){
        this.fstatus = fstatus;
    }

//=================================================================//
// method

    @Override
    public boolean isFile(){
        return this.fstatus.isFile();
    }

    @Override
    public boolean isDir(){
        return this.fstatus.isDirectory();
    }

    @Override
    public String getPath(){
        return this.fstatus.getPath().toString();
    }

}