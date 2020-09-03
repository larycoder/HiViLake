package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * FileWrapper represent the file in hadoop filesystem
 *
 */

import org.apache.hadoop.fs.*;

import java.io.InputStream;
import java.io.IOException;

// Storage wrapper interface
import com.usth.hieplnc.storage.api.filesystem.SWFile;

public class FileWrapper implements SWFile{
// vairable

    private final FileSystem fs;
    private final Path path;

//=================================================================//
// constructor

    public FileWrapper(FileSystem fs, Path path) throws IOException{
        this.fs = fs;

        if(this.fs.getFileStatus(path).isDirectory() == true){
            throw new IOException("Class requests the path is File not Dir");
        }

        this.path = path;
    }

//=================================================================//
// method

    @Override
    public InputStream readStream() throws IOException{
        return this.fs.open(this.path);
    }

    @Override
    public void append(InputStream streamData) throws IOException{
        FSDataOutputStream appendData = this.fs.append(this.path);
        byte[] buffer = new byte[1024]; // middle buffer to transfer data
        int len = 0;

        // transfer data from input steam to file
        while((len = streamData.read(buffer)) > 0){
            appendData.write(buffer, 0, len);
        }
        appendData.hflush();
        appendData.hsync();
        appendData.close();
    }

    @Override
    public void writeStream(InputStream streamData) throws IOException{
        FSDataOutputStream writeData = this.fs.create(this.path);
        byte[] buffer = new byte[1024]; // middle buffer to transfer data
        int len = 0;

        // transfer data
        while((len = streamData.read(buffer)) > 0){
            writeData.write(buffer, 0, len);
        }
        writeData.hflush();
        writeData.hsync();
        writeData.close();
    }

}