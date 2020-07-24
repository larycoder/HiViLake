package com.usth.hieplnc.metadata.spark.delta;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.DataTypes;

import org.apache.spark.sql.functions;
import org.apache.spark.sql.Column;

import io.delta.tables.DeltaTable;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.lang.NullPointerException;

import com.usth.hieplnc.metadata.spark.delta.DeltaWrapper;

public class BasicMetadata{
// variable

    private String deltaPos = null;
    private SparkSession context = null;
    private Dataset<Row> metaTable = null;
    private DeltaWrapper metaDeltaTable = null;

//=============================================================================//
// constructor

    public BasicMetadata(SparkSession context){
        if(context == null) throw new NullPointerException("The spark session is not exists");
        this.context = context;
    }

    public BasicMetadata(SparkSession context, String position){
        this(context);
        this.deltaPos = position;
    }

//=============================================================================//
// method

    public BasicMetadata setPos(String position){
        this.deltaPos = position;
        return this;
    }

    public String getPos(){
        return deltaPos;
    }

    private void loadTable(){
        if(deltaPos == null) throw new NullPointerException("The metadata position is not exists");
        metaTable = context.read().format("delta").load(deltaPos);
    }

    private void loadDelta(){
        if(deltaPos == null) throw new NullPointerException("The metadata position is not exists");
        metaDeltaTable = new DeltaWrapper(DeltaTable.forPath(context, deltaPos));
    }

    public BasicMetadata load(){
        loadTable();
        loadDelta();
        return this;
    }

    public void update(String condition, HashMap<String, String> listValue){
        if(metaDeltaTable == null) throw new NullPointerException("DeltaTable is not exists");
        metaDeltaTable.update(condition, listValue);
    }

    private void createTable(List<Row> data, StructType schema){
        metaTable = context.createDataFrame(data, schema);
    }

    private void createTable(List<List<String>> data, List<String> schema){
        List<Row> tableData = new ArrayList<>();
        for(List<String> i : data){
            String[] arrayData = new String[i.size()];
            i.toArray(arrayData);
            tableData.add(RowFactory.create(arrayData));
        }

        StructType tableSchema = new StructType();
        for(String i : schema){
            tableSchema = tableSchema.add(i, DataTypes.StringType);
        }

        createTable(tableData, tableSchema);
    }

    public BasicMetadata createTable(List<String> schema){
        StructType tableSchema = new StructType();
        for(String i : schema){
            tableSchema = tableSchema.add(i, DataTypes.StringType);
        }
        createTable(new ArrayList<Row>(), tableSchema);
        return this;
    }

    public Dataset<Row> getTable(){ return metaTable; }

    public DeltaTable getDelta(){ return metaDeltaTable.getDelta(); }

    public List<String> getSchema(){
        if(metaDeltaTable == null) throw new NullPointerException("DeltaTable is not loaded");
        return metaDeltaTable.getListField();
    }

    public void insert(List<List<String>> data, List<String> schema){
        if(metaTable == null) throw new NullPointerException("The metadata is not exists");
        else if(deltaPos == null) throw new NullPointerException("The metadata position is not exists");
        Dataset<Row> temp = metaTable;
        createTable(data, schema);
        metaTable.write().format("delta").mode("append").save(deltaPos);
        metaTable = temp;
    }

    public void delete(String condition){
        if(metaDeltaTable == null) throw new NullPointerException("DeltaTable is not loaded");
        metaDeltaTable.delete(condition);
    }

    public void saveTable(){
        if(metaTable == null) throw new NullPointerException("The metadata is not exists");
        else if(deltaPos == null) throw new NullPointerException("The metadata position is not exists");
        metaTable.write().format("delta").save(deltaPos);
    }

    public static void main(String[] args){
        SparkSession mySession = SparkSession.builder().master("local[*]").appName("metadata test").config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension").config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog").getOrCreate();
        BasicMetadata myMeta = new BasicMetadata(mySession);

        /*
            * create function
            *
        */
        // List<String> testSchema = new ArrayList<>(Arrays.asList("name", "age", "university"));
        // myMeta.setPos("/tmp/metadata/firstDelta").createTable(testSchema).saveTable();

        /*
            * load function
            *
        */
        myMeta.setPos("/tmp/metadata/firstDelta").load();
        
        /*
            * insert function
            *
        */
        // List<List<String>> testData = new ArrayList<>();
        // testData.add(Arrays.asList("hiep", "21", "usth"));
        // testData.add(Arrays.asList("aback", "40", "ede"));
        // testData.add(Arrays.asList("Dang A Thao", "3", "mongnguyen"));
        // testData.add(Arrays.asList("Pham Mai Mai", "name + 3", "japon"));
        // myMeta.insert(testData, myMeta.getSchema());

        /*
            * update function
            *
        */
        // myMeta.update("name == 3", new HashMap<String, String>(){{
        //         put("university", "Bach_gia");
        //         put("age", "30");
        //     }}
        // );

        /*
            * delete function
            *
        */
        // myMeta.delete("age == 'name + 3' ");

        myMeta.getDelta().toDF().show();
        
        // close context
        mySession.close();
    }

}
