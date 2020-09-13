package com.usth.hieplnc.storage.hadoop.sql;

/**
 * DOC:
 * - CSV file parser
 *
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;
import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.sql.SqlParser;
import com.usth.hieplnc.storage.api.filesystem.FilesystemWrapper;
import com.usth.hieplnc.storage.api.filesystem.SWFile;
import com.usth.hieplnc.storage.api.filesystem.model.*;

/**
 * WARNNING:
 * - CSV loader still use default
 *
 */

public class CSVFileParser implements SqlParser{
// variable
    private final FilesystemWrapper fs;

//=================================================================//
// constructor
    public CSVFileParser(FilesystemWrapper fs){
        this.fs = fs;
    }

//=================================================================//
// method
    @Override
    public void save(String path, String name, JSONObject schema, JSONObject data) throws IOException{
        // setup
        List<String> fieldList = (List<String>) schema.get("fields");
        String[] fields = fieldList.toArray(new String[0]);
        List<List<String>> rowList = (List<List<String>>) data.get("data");
        StringBuffer file = new StringBuffer();

        // push data to file
        CSVPrinter printer = new CSVPrinter(file, CSVFormat.DEFAULT.withHeader(fields));
        for(List row: (List<List<String>>) rowList){
            printer.printRecord(row);
        }
        printer.flush();

        // save file
        String pathFile = (path.charAt(path.length() - 1) == '/') ? path + name + ".csv" : path + "/" + name + ".csv";
        if(!this.fs.exists(pathFile)){
            this.fs.createPath(pathFile, PathType.FILE);
        }

        this.fs.openFile(pathFile).writeStream(new ByteArrayInputStream(file.toString().getBytes()));
    }

    @Override
    public JSONObject load(String path) throws IOException{
        // load data from file to buffer
        Reader file = new InputStreamReader(this.fs.openFile(path).readStream());
        CSVParser csvParser = new CSVParser(file, CSVFormat.DEFAULT);

        // prepare space for data loading
        JSONObject dataFrame = new JSONObject();
        List fields = new ArrayList<String>();
        List data = new ArrayList<ArrayList<String>>();

        int skip = 0;
        for(CSVRecord row: csvParser){
            // create fields name
            if(skip == 0){
                for(int i = 0; i < row.size(); i++){
                    fields.add("Column_" + String.valueOf(i));
                }
                skip = 1;
            }
            
            // load data to data frame
            List rowValue = new ArrayList<String>();
            for(String value: row){
                rowValue.add(value);
            }
            data.add(rowValue);
        }

        dataFrame.put("fields", fields);
        dataFrame.put("data", data);

        // end connection
        csvParser.close();
        return dataFrame;
    }
}