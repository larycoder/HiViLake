package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * ListPathWrapper represent list of file status in hadoop fs
 *
 */

import org.apache.hadoop.fs.*;

import com.usth.hieplnc.storage.hadoop.FilestatusWrapper;

// Storage wrapper interface
import com.usth.hieplnc.storage.api.filesystem.*;

public class ListPathWrapper implements SWListPath{
// variable

    private final FileStatus[] listStatus;

//=================================================================//
// constructor

    public ListPathWrapper(FileStatus[] listStatus){
        this.listStatus = listStatus;
    }

//=================================================================//
//mehtod

    @Override
    public SWFilestatus get(int index){
        return new FilestatusWrapper(this.listStatus[index]);
    }

    @Override
    public int size(){
        return this.listStatus.length;
    }

}