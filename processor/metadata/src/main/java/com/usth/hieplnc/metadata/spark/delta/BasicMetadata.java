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

public class BasicMetadata{
// variable

//=============================================================================//
// constructor

//=============================================================================//
// method

    public static void main(String[] args){
        SparkSession mySession = SparkSession.builder().master("local[*]").appName("metadata test").getOrCreate();

        // create dataframe
        //Encoder<Employee> empEncoder = Encoders.bean(Employee.class);
        //Dataset<Row> empDF = mySession.createDataset(empList, Encoders.STRING()).toDF();
        //empDF.write().format("delta").save("/tmp/metadata/firstDelta");
        //empDF.write().format("delta").mode("append").save("/tmp/metadata/firstDelta");
        //empDF = mySession.read().format("delta").load("/tmp/metadata/firstDelta");
        
        List<Row> listTest = new ArrayList<>();
        listTest.add(RowFactory.create("hiep", "62", "usth"));
        listTest.add(RowFactory.create("ngoc", "63", "usth"));
        listTest.add(RowFactory.create("bach", "22", "usth"));

        StructType testSchema = new StructType();
        testSchema = testSchema.add("name", DataTypes.StringType);
        testSchema = testSchema.add("id", DataTypes.StringType);
        testSchema = testSchema.add("school", DataTypes.StringType);

        Dataset<Row> empDF = mySession.createDataFrame(listTest, testSchema);
        empDF.show();

        empDF.write().format("delta").save("/tmp/metadata/firstDelta");
        
        // close context
        mySession.close();
    }

}
