package com.usth.hieplnc.metadata.spark.delta;

import io.delta.tables.DeltaTable;

import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DeltaWrapper{
// variable

    private DeltaTable table = null;

//=================================================================//
// constructor

    public DeltaWrapper(DeltaTable table){
        this.table = table;
    }
    
//=================================================================//
// method

    public DeltaTable getDelta(){ return table; }

    public List<String> getListField(){
        return Arrays.asList(table.toDF().schema().names());
    }

    public void update(String condition, HashMap<String, String> listValue){
        HashMap<String, Column> columnData = new HashMap<>();
        for(String columnName : listValue.keySet()){
            columnData.put(columnName, functions.expr("'" + listValue.get(columnName) + "'"));
        }

        table.update(
            functions.expr(condition),
            columnData
        );
    }

    public void delete(String condition){ table.delete(condition); }
    
}