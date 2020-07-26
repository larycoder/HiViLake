package com.usth.hieplnc.common.hadoop;

import com.usth.hieplnc.common.hadoop.FinderResult;
import com.usth.hieplnc.common.hadoop.FileFinder;

import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.hadoop.fs.*;

public class FileWrapper extends FileStatus implements FinderResult{
// variable

    private FileFinder finder = null;
    private FileSystem fs = null;

//=============================================================//
// constructor

    public FileWrapper(FileFinder finder){
        this.finder = finder;
        this.fs = finder.getFilesystem();
    }

    public FileWrapper(FileStatus status, FileSystem fs) throws IOException{
        super(status);
        this.fs = fs;
    }

//=============================================================//
// method

    @Override
    public boolean next() throws FileNotFoundException, IOException{
        if(finder == null) throw new NullPointerException("FileFinder is not exists");
        return finder.next();
    }

    @Override
    public FileWrapper getFile() throws IOException{
        if(finder == null) throw new NullPointerException("FileFinder is not exists");
        return new FileWrapper(finder.getFile(), finder.getFilesystem());
    }

    @Override
    public FileStatus getStatus() throws IOException{
        if(finder == null) throw new NullPointerException("FileFinder is not exists");
        return finder.getFile();
    }

    public FileWrapper[] listFile() throws FileNotFoundException, IOException{
        FileStatus[] listStatus = fs.listStatus(getPath());
        FileWrapper[] listWrapper = new FileWrapper[listStatus.length];
        // Path need to improve
        //==========================================================================//
        for(int i = 0; i < listStatus.length; i++){
            listWrapper[i] = new FileWrapper(listStatus[i], fs);
        }
        //==========================================================================//
        return listWrapper;
    }

    public FileStatus[] listStatus() throws FileNotFoundException, IOException{
        return fs.listStatus(getPath());
    }

    public FileSystem getFilesystem(){ return fs; }

}