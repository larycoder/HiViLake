package com.usth.hieplnc.storage.hadoop.sql;

/**
 * DOC:
 * - Class load filesystem meta as tabular file
 *
 */

import java.lang.UnsupportedOperationException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.hadoop.fs.*;
import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlParser;

public class FsMetaFileParser implements SqlParser{
// variable
    private final FileSystem fs;

//=================================================================//
// constructor
    public FsMetaFileParser(FileSystem fs){
        this.fs = fs;
    }

//=================================================================//
// method
    @Override
    public void save(String path, String name, JSONObject schema, JSONObject data) throws IOException{
        throw new UnsupportedOperationException("This parser does not support save function");
    }

    @Override
    public JSONObject load(String path) throws IOException{
        // get FileStatus of path
        Path filePath = new Path(path);
        FileStatus pathStatus = this.fs.getFileStatus(filePath);
        
        // build dataframe
        JSONObject dataFrame = new JSONObject();
        if(pathStatus.isFile()){

            // build name
            dataFrame.put("name", "File_" + filePath.getName());

            // build fields
            List<String> fields = new ArrayList<String>();
            fields.add("name");
            fields.add("path");
            fields.add("code");
            fields.add("isAbsolute");
            fields.add("isRoot");
            fields.add("accessTime");
            fields.add("blockSize");
            fields.add("group");
            fields.add("bytesLenght");
            fields.add("modificationTime");
            fields.add("owner");
            fields.add("replication");
            fields.add("encrypted");
            dataFrame.put("fields", fields);

            // build data
            List<String> row = new ArrayList<String>();
            row.add(filePath.getName());
            row.add(filePath.toString());
            row.add(Integer.toString(filePath.hashCode()));
            row.add(filePath.isAbsolute() ? "1" : "0");
            row.add(filePath.isRoot() ? "1" : "0");
            row.add(Long.toString(pathStatus.getAccessTime()));
            row.add(Long.toString(pathStatus.getBlockSize()));
            row.add(pathStatus.getGroup());
            row.add(Long.toString(pathStatus.getLen()));
            row.add(Long.toString(pathStatus.getModificationTime()));
            row.add(pathStatus.getOwner());
            row.add(String.valueOf(pathStatus.getReplication()));
            row.add(pathStatus.isEncrypted() ? "1" : "0");
            List<List<String>> data = new ArrayList<List<String>>();
            data.add(row);
            dataFrame.put("data", data);

        } else{
            // build name
            dataFrame.put("name", "Dir_" + filePath.getName());

            // build fields
            List<String> fields = new ArrayList<String>();
            fields.add("name");
            fields.add("type");
            fields.add("path");
            fields.add("code");
            fields.add("isAbsolute");
            fields.add("isRoot");
            fields.add("accessTime");
            fields.add("blockSize");
            fields.add("group");
            fields.add("bytesLenght");
            fields.add("modificationTime");
            fields.add("owner");
            fields.add("replication");
            fields.add("encrypted");
            dataFrame.put("fields", fields);

            // build data
            FileStatus[] listStatus = this.fs.listStatus(filePath);
            List<List<String>> data = new ArrayList<List<String>>();

            for(FileStatus status: listStatus){
                List<String> row = new ArrayList<String>();
                row.add(filePath.getName());
                row.add(status.isFile() ? "file" : "dir");
                row.add(filePath.toString());
                row.add(Integer.toString(filePath.hashCode()));
                row.add(filePath.isAbsolute() ? "1" : "0");
                row.add(filePath.isRoot() ? "1" : "0");
                row.add(Long.toString(status.getAccessTime()));
                row.add(Long.toString(status.getBlockSize()));
                row.add(status.getGroup());
                row.add(Long.toString(status.getLen()));
                row.add(Long.toString(status.getModificationTime()));
                row.add(status.getOwner());
                row.add(String.valueOf(status.getReplication()));
                row.add(status.isEncrypted() ? "1" : "0");
                data.add(row);
            }

            dataFrame.put("data", data);
        }

        return dataFrame;
    }

}