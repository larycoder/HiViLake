/*
    * interface use to get result file from pattern
    *
*/
package com.usth.hieplnc.common.hadoop;

import com.usth.hieplnc.common.hadoop.FileWrapper;

import org.apache.hadoop.fs.FileStatus;

import java.io.IOException;
import java.io.FileNotFoundException;

public interface FinderResult{
    public boolean next() throws FileNotFoundException, IOException; // check if file is available to get
    public FileWrapper getFile() throws IOException; // get file from file list
    public FileStatus getStatus() throws IOException; // get status from list
    public String getStringFile() throws IOException; // get file in form of string
}