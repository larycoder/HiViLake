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
import com.usth.hieplnc.storage.api.sql.*;
import com.usth.hieplnc.storage.api.sql.model.*;

import com.usth.hieplnc.storage.hadoop.FileWrapper;
import com.usth.hieplnc.storage.hadoop.FileFinder;
import com.usth.hieplnc.storage.hadoop.sql.*;

public class Storage implements FilesystemWrapper, SqlWrapper{
// variable
    public static final String CSVParser = "csv";
    public static final String FsMetaParser = "fsmeta";
    public static final String VirtualParser = "virtual";

    private final Configuration conf;
    private final FileSystem fs;

    private FileFinder searchEngine;
    
    private JSONObject parsers;

//=================================================================//
// constructor

    public Storage(Configuration conf) throws IOException{
        this.conf = conf;
        this.fs = (new Path("hdfs://")).getFileSystem(this.conf);

        this.searchEngine = new FileFinder(this.fs);

        // add sql parser
        List<String> parserList = new ArrayList<String>();
        parserList.add(this.CSVParser);
        parserList.add(this.FsMetaParser);
        parserList.add(this.VirtualParser);

        this.parsers = new JSONObject();
        this.parsers.put("parser", parserList);
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

    @Override
    public List<JSONObject> listParser(){
        List<JSONObject> result = new ArrayList<JSONObject>();
        result.add(this.parsers);
        return result;
    }

    @Override
    public SqlParser getParser(int index){
        List<String> parsers = (List<String>) this.parsers.get("parser");
        String parser = parsers.get(index);
        if(parser.equals(this.CSVParser)){
            return new CSVFileParser(this);
        } else if(parser.equals(this.FsMetaParser)){
            return new FsMetaFileParser(this.fs);
        } else if(parser.equals(this.VirtualParser)){
            return new VirtualFileParser();
        } else{
            return null;
        }
    }

    @Override
    public SqlTable use(String path, JSONObject extra){
        String parser = null;
        String tableName = null;

        // get parser provided
        if(extra != null){
            if(extra.containsKey("parser")){
                parser = (String) extra.get("parser");
            }

            if(extra.containsKey("tableName")){
                tableName = (String) extra.get("tableName");
            }
        }

        // get parser from extension
        if(parser == null){
            Path pathFile = new Path(path);
            String fileName = pathFile.getName();
            String[] splitString = fileName.split(".");
            if(splitString[splitString.length - 1] == "csv"){
                parser = this.CSVParser;
            }

            tableName = splitString[0];
        }

        // check value
        if(tableName == null || parser == null) return null;
        List<String> parsers = (List<String>) this.parsers.get("parser");
        return new SqlTableWrapper(tableName, getParser(parsers.indexOf(parser)), path);
    }

    @Override
    public SqlTable use(SqlResult table){
        SqlParser parser = new VirtualFileParser();
        try{
            parser.save(null, null, table.getSchema(), table.getData());
        } catch(Exception e){;}

        return new SqlTableWrapper((String) table.getSchema().get("name"), parser, null);
    }

    @Override
    public void createTable(String path, String name, JSONObject schema, SqlParser parser) throws IOException{
        List<List<String>> empty = new ArrayList<List<String>>();
        JSONObject data = new JSONObject();
        data.put("data", empty);
        parser.save(path, name, schema, data);
    }

    @Override
    public void addTable(String path, String name, SqlResult table, SqlParser parser) throws IOException{
        parser.save(path, name, table.getSchema(), table.getData());
    }

    @Override
    public void dropTable(String path) throws IOException{
        deletePath(path, SWOption.ALL);
    }

    public static void main(String args[]) throws IOException, HVSqlException{
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        conf.set("dfs.replication", "1");
        Storage test = new Storage(conf);

        JSONObject extra = new JSONObject();
        extra.put("tableName", "test");
        extra.put("parser", test.FsMetaParser);
        SqlTable dirTable = test.use("/", extra);

        Col nameCol = new Col("name", ColType.REAL, DataType.STRING);
        Col pathCol = new Col("path", ColType.REAL, DataType.STRING);
        List<Col> listCol = new ArrayList<Col>();
        listCol.add(nameCol);
        listCol.add(pathCol);

        JSONObject extra2 = new JSONObject();
        extra2.put("tableName", "test2");
        extra2.put("parser", test.FsMetaParser);
        SqlTable dirTable2 = test.use("/", extra2);
        dirTable.unionAll(dirTable2);

        SqlResult result;

        try{
            result = dirTable.commit();
            SqlTable unionTable = test.use(result);
            unionTable.select(listCol, new SqlFunc());
            result = unionTable.commit();
        } catch(Exception e){
            test.close();
            e.printStackTrace();
            return;
        }
        List<String> fields = (List<String>) result.getSchema().get("fields");
        List<List<String>> data = (List<List<String>>) result.getData().get("data");
        for(String field: fields){
            System.out.print(field + " | ");
        }

        for(List<String> row: data){
            System.out.println("");
            for(String value: row){
                System.out.print(value + " | ");
            }
        }
        System.out.println("");
        test.close();
    }
}
