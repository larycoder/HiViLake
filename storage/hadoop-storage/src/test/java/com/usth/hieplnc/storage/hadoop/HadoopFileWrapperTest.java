package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * The class provide junit test for StorageWrapper
 *
 */

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.Configuration;

import com.usth.hieplnc.storage.api.filesystem.*;
import com.usth.hieplnc.storage.api.filesystem.model.*;

public class HadoopFileWrapperTest{
    private static FilesystemWrapper fsStore;
    private static FileSystem fs;
    private static SWFile file;

    @BeforeClass
    public static void setUp(){
        try{
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://localhost:9000");
            conf.set("dfs.replication", "1");
            fs = (new Path("hdfs://")).getFileSystem(conf);

            StorageFactory factory = new StorageFactory();
            factory.setParam("fs.defaultFS", "hdfs://localhost:9000");
            factory.setParam("dfs.replication", "1");
            fsStore = (FilesystemWrapper) factory.newInstance();
            fsStore.createPath("/demo.dat", PathType.FILE);
            file = fsStore.openFile("/demo.dat");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void modifyFile(){
        try{
            // test write data to empty file
            String data = "Le Nhu Chu Hiep Message";
            byte[] byteData = data.getBytes();
            InputStream inputData = new ByteArrayInputStream(byteData);
            file.writeStream(inputData);

            InputStream readData = fs.open(new Path("/demo.dat"));
            byte[] buffer = new byte[1024];

            int len = 0;
            String outputData = "";
            while((len = readData.read(buffer)) > 0){
                outputData += new String(Arrays.copyOfRange(buffer, 0, len));
            }
            readData.close();
            assertTrue("test write data to hadoop fs", data.equals(outputData));

            // test append data
            String appendString = " - append data";
            byteData = appendString.getBytes();
            InputStream appendData = new ByteArrayInputStream(byteData);
            file.append(appendData);

            readData = fs.open(new Path("/demo.dat"));
            outputData = "";
            while((len = readData.read(buffer)) > 0){
                outputData += new String(Arrays.copyOfRange(buffer, 0, len));
            }
            readData.close();
            assertTrue("test append data to hadoop fs", (data + appendString).equals(outputData));

            // write data to non-empty file
            String reData = "message: rewrite Data";
            byteData = reData.getBytes();
            InputStream rewriteData = new ByteArrayInputStream(byteData);
            file.writeStream(rewriteData);

            readData = fs.open(new Path("/demo.dat"));
            outputData = "";
            while((len = readData.read(buffer)) > 0){
                outputData += new String(Arrays.copyOfRange(buffer, 0, len));
            }
            readData.close();
            assertTrue("test write data to non-empty file in hdfs", outputData.equals(reData));

            // read data from file
            readData = file.readStream();
            outputData = "";
            while((len = readData.read(buffer)) > 0){
                outputData += new String(Arrays.copyOfRange(buffer, 0, len));
            }
            readData.close();
            assertTrue("test read data from hadoop file", outputData.equals(reData));

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown(){
        try{
            fsStore.deletePath("/demo.dat", SWOption.ALL);
            fs.close();
            fsStore.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}