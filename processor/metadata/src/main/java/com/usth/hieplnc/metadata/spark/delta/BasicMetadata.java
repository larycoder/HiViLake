package com.usth.hieplnc.metadata.spark.delta;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.Metadata;

import org.apache.spark.sql.types.DataTypes;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.NullPointerException;

public class BasicMetadata{
// variable

    private String deltaPos = null;
    private SparkSession context = null;
    private Dataset<Row> metaTable = null;

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

    public void setPos(String position){
        this.deltaPos = position;
    }

    public String getPos(){
        return deltaPos;
    }

    public void loadTable(){
        if(deltaPos == null) throw new NullPointerException("The metadata position is not exists");
        metaTable = context.read().format("delta").load(deltaPos);
    }

    private void createTable(List<Row> data, StructType schema){
        metaTable = context.createDataFrame(data, schema);
    }

    public void createTable(List<List<String>> data, List<String> schema){
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

    public Dataset<Row> getTable(){ return metaTable; }

    public void saveTable(){
        if(metaTable == null) throw new NullPointerException("The metadata is not exists");
        else if(deltaPos == null) throw new NullPointerException("The metadata position is not exists");
        metaTable.write().format("delta").save(deltaPos);
    }

    public static void main(String[] args){
        SparkSession mySession = SparkSession.builder().master("local[*]").appName("metadata test").getOrCreate();

        BasicMetadata myMeta = new BasicMetadata(mySession);

        List<List<String>> testData = new ArrayList<>();
        testData.add(Arrays.asList("hiep", "21", "usth"));
        testData.add(Arrays.asList("huyen", "21", "usth"));
        testData.add(Arrays.asList("bach", "21", "usth"));
        List<String> testSchema = new ArrayList<>(Arrays.asList("name", "age", "university"));

        myMeta.createTable(testData, testSchema);
        Dataset<Row> empDF = myMeta.getTable();

        //empDF.write().format("delta").save("/tmp/metadata/firstDelta");
        //empDF.write().format("delta").mode("append").save("/tmp/metadata/firstDelta");
        // empDF = mySession.read().format("delta").load("/tmp/metadata/firstDelta");
        empDF.show();
        
        // close context
        mySession.close();
    }

}
