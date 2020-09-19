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
    private List<Integer> parserCode;

//=================================================================//
// constructor

    public Storage(Configuration conf) throws IOException{
        this.conf = conf;
        this.fs = (new Path("hdfs://")).getFileSystem(this.conf);

        this.searchEngine = new FileFinder(this.fs);

        // build sql parser
        List<String> parserList = new ArrayList<String>();
        parserList.add(this.CSVParser);
        parserList.add(this.FsMetaParser);
        parserList.add(this.VirtualParser);
        this.parsers = new JSONObject();
        this.parsers.put("parser", parserList);

        // build sql parser code
        this.parserCode = new ArrayList<Integer>();
        this.parserCode.add(0);
        this.parserCode.add(50);
        this.parserCode.add(100);
    }

//=================================================================//
// method
    private JSONObject genParserJson(String parserName, int parserCode){
        JSONObject parser = new JSONObject();
        parser.put("parser", parserName);
        parser.put("code", parserCode);
        return parser;
    }

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
    public void movePath(String desc, String src, int option) throws IOException{
        if(!this.fs.rename(new Path(src), new Path(desc))){
            throw new IOException("something wrong with this action !");
        }
    }
    
    @Override
    public SWFile openFile(String path) throws IOException{
        return new FileWrapper(this.fs, new Path(path));
    }

    @Override
    public boolean exists(String path) throws IOException{
        Path pathFile = new Path(path);
        return this.fs.exists(pathFile);
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
        // build parser list
        List<JSONObject> parserList = new ArrayList<JSONObject>();
        List<String> parsers = (List<String>) this.parsers.get("parser");
        for(int i = 0; i < parsers.size(); i++){
            parserList.add(genParserJson(parsers.get(i), this.parserCode.get(i)));
        }
        return parserList;
    }

    @Override
    public SqlParser getParser(int index){
        // get index from code
        int code = index;
        index = this.parserCode.indexOf(code);

        // get parser
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
        SqlParser parser = null;
        String tableName = null;

        // get parser provided
        if(extra != null){
            if(extra.containsKey("parser")){
                String parserString = (String) extra.get("parser");
                List<String> parsers = (List<String>) this.parsers.get("parser");
                int index = parsers.indexOf(parserString);
                parser = getParser(this.parserCode.get(index));
            } else if(extra.containsKey("parserCode")){
                parser = getParser((int) extra.get("parserCode"));
            }

            if(extra.containsKey("tableName")){
                tableName = (String) extra.get("tableName");
            }
        }

        // get parser from extension
        if(parser == null || tableName == null){
            Path pathFile = new Path(path);
            String fileName = pathFile.getName();
            String[] splitString = fileName.split("\\.");
            if(parser == null){
                if(splitString[splitString.length - 1].equals("csv")){
                    parser = getParser(0);
                }
            }
            if(tableName == null){
                tableName = splitString[0];
            }
        }

        // check value
        if(tableName == null || parser == null) return null;
        return new SqlTableWrapper(tableName, parser, path);
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

    /**
     * Test: because the limit of time, testing will appear in here
     *
     */
    public static void main(String args[]) throws IOException, HVSqlException{
        // open storage
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        conf.set("dfs.replication", "1");
        Storage test = new Storage(conf);

        // prepare table
        JSONObject extra = new JSONObject();
        extra.put("tableName", "test");
        extra.put("parser", test.FsMetaParser);
        SqlTable dirTable = test.use("/", extra);

        // choose column
        Col nameCol = new Col("name", ColType.REAL, DataType.STRING);
        Col typeCol = new Col("type", ColType.REAL, DataType.STRING);
        // Col pathCol = new Col("path", ColType.REAL, DataType.STRING);
        List<Col> listCol = new ArrayList<Col>();
        listCol.add(nameCol);
        listCol.add(typeCol);
        // listCol.add(pathCol);

        // condition
        SqlFunc whereCondition = new SqlFunc();

        // query
        dirTable.select(listCol, whereCondition);

        // commit
        SqlResult result;
        try{
            result = dirTable.commit();
            SqlTable table_1 = test.use(result).as("table_1");
            SqlTable table_2 = test.use(result).as("table_2");

            // inner join 2 table
            table_1.join(table_2, "name", "name");
            result = table_1.commit();

            // save data
            test.addTable("/demo", "table_1", result, test.getParser(0));

            // load data from csv file
            result = test.use("/demo/table_1.csv", null).commit();
        } catch(Exception e){
            test.close();
            e.printStackTrace();
            return;
        }

        // print result
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

        // close
        test.close();
    }
}
