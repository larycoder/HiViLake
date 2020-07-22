package com.usth.hieplnc.spark.demo;

import java.util.List;
import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.launcher.SparkLauncher;

public class MyFirstSpark{
// variable



//=====================================================================//
//constructor



//=====================================================================//
// method

    public static void main(String[] args){
        System.out.println("hello spark");

        // initializing spark
        SparkConf mySparkConf = new SparkConf().setAppName("myFristSpark").setMaster("yarn");
        JavaSparkContext mySparkContext = new JavaSparkContext(mySparkConf);

        // create RDD by: Parallelized Collections
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> distData = mySparkContext.parallelize(data);
        int sumValue = distData.reduce((a, b) -> a + b);
        System.out.println("Spark compute output: " + sumValue);

        // stop spark
        mySparkContext.stop();
        System.getProperties().list(System.out);
    }

}
