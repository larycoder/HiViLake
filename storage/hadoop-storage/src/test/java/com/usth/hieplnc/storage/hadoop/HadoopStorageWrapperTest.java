package com.usth.hieplnc.storage.hadoop;

/**
 * Doc:
 * The class provide junit test for StorageWrapper
 *
 */

import java.util.List;
import java.util.Iterator;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.json.simple.JSONObject;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.Configuration;

import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.api.filesystem.*;
import com.usth.hieplnc.storage.api.filesystem.model.*;

public class HadoopStorageWrapperTest{
    // test class
    private static StorageWrapper testStore;
    // assert class
    private static FileSystem fs;

    @BeforeClass
    public static void setUp(){
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        StorageFactory factory = new StorageFactory();
        factory.setParam("fs.defaultFS", "hdfs://localhost:9000");

        try{
            fs = (new Path("hdfs://")).getFileSystem(conf);
            HadoopStorageWrapperTest.testStore = factory.newInstance();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void checkSupportStorageType(){
        // exec
        List supportList = this.testStore.support();
        // assert
        assertTrue("Test storage support filesystem", supportList.contains("filesystem"));
        assertTrue("Test storage support sql", supportList.contains("sql"));
    }

    @Test
    public void modifyFileInHadoop(){
        try{
            // exec
            ((FilesystemWrapper) testStore).createPath("/demo.test", PathType.FILE);
            
            Path file = new Path("/demo.test");
            FileStatus fstatus = fs.getFileStatus(file);

            // assert
            assertTrue("test new path added is file", fstatus.isFile());

            // exec
            ((FilesystemWrapper) testStore).deletePath("/demo.test", SWOption.ALL);

            // assert
            assertFalse("test path deleted", fs.exists(file));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createNestedFileInHadoop(){
        try{
            // exec
            ((FilesystemWrapper) testStore).createPath("/demom/demo.test", PathType.FILE);
            
            Path file = new Path("/demo/demo.test");
            FileStatus fstatus = fs.getFileStatus(file);

            // assert
            assertTrue("test new nested path added is file", fstatus.isFile());

            // exec
            ((FilesystemWrapper) testStore).deletePath("/demo", SWOption.ALL);

            // assert
            assertFalse("test nested path deleted", fs.exists(file));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void modifyEmptyDirInHadoop(){
        try{
            // exec
            ((FilesystemWrapper) testStore).createPath("/demo", PathType.DIR);
            
            Path file = new Path("/demo");
            FileStatus fstatus = fs.getFileStatus(file);

            // assert
            assertTrue("test new path added is dir", fstatus.isDirectory());

            // exec
            ((FilesystemWrapper) testStore).deletePath("/demo", SWOption.EMPTY);

            // assert
            assertFalse("test path deleted", fs.exists(file));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void createNestedDirInHadoop(){
        try{
            // exec
            ((FilesystemWrapper) testStore).createPath("/demo/hello/", PathType.DIR);
            
            Path file = new Path("/demo/hello/");
            FileStatus fstatus = fs.getFileStatus(file);

            // assert
            assertTrue("test new nested path added is dir", fstatus.isDirectory());

            // exec
            ((FilesystemWrapper) testStore).deletePath("/demo", SWOption.ALL);

            // assert
            assertFalse("test nested path deleted", fs.exists(file));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void deleteNestedDirInHadoopWithSWOptionIsEmpty(){
        boolean exceptionCheck = false;

        try{
            // exec
            ((FilesystemWrapper) testStore).createPath("/demo/hello/", PathType.DIR);
            
            Path file = new Path("/demo/hello/");
            FileStatus fstatus = fs.getFileStatus(file);

            // assert
            assertTrue("test new nested path added is dir", fstatus.isDirectory());

            // exec
            ((FilesystemWrapper) testStore).deletePath("/demo", SWOption.EMPTY);

            // assert
        } catch(Exception e){
            exceptionCheck = (e instanceof IOException);
        }

        assertTrue("test nested path deleted is false", exceptionCheck);

        // clean
        try{
            ((FilesystemWrapper) testStore).deletePath("/demo", SWOption.ALL);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void openFile(){
        try{
            // exec
            ((FilesystemWrapper) testStore).createPath("/demo/test.dat", PathType.FILE);
            
            Path file = new Path("/demo/test.dat");
            FileStatus fstatus = fs.getFileStatus(file);

            SWFile hadoopFile = ((FilesystemWrapper) testStore).openFile("/demo/test.dat");

            // assert
            assertTrue("test open file in hadoop", hadoopFile instanceof FileWrapper);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void checkStatusOfPathInStorageWrapper(){
        try{
            // exec
            FilesystemWrapper fsStore = (FilesystemWrapper) testStore;
            fsStore.createPath("/demo/test1", PathType.FILE);
            fsStore.createPath("/demo/test2", PathType.FILE);
            fsStore.createPath("/demo/test3", PathType.FILE);
            SWFilestatus fstatus = fsStore.getStatus("/demo");
            SWListPath listFstatus = fsStore.listStatus(fstatus);

            FileStatus[] rfstatus = fs.listStatus(new Path("/demo"));

            // assert
            assertEquals("test number of file in status is equal", rfstatus.length, listFstatus.size());
            assertEquals("test path name of file", rfstatus[0].getPath().toString(), listFstatus.get(0).getPath());

            fsStore.deletePath("/demo", SWOption.ALL);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchFileOfHadoopFS(){
        try{
            // exec
            FilesystemWrapper fsStore = (FilesystemWrapper) testStore;
            fsStore.createPath("/demo/test1", PathType.FILE);
            fsStore.createPath("/demo/test2", PathType.FILE);
            fsStore.createPath("/demo/test3", PathType.FILE);
            JSONObject pattern = new JSONObject();
            pattern.put("pattern", "/demo/test*");
            Iterator resultSearch = fsStore.searchFile(pattern);

            FileStatus[] rfstatus = fs.listStatus(new Path("/demo"));

            int i = 2;
            while(resultSearch.hasNext()){
                assertEquals("test search", rfstatus[i].getPath().toString(), ((SWFilestatus) resultSearch.next()).getPath());
                i--;
            }

            fsStore.deletePath("/demo", SWOption.ALL);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown(){
        try{
            HadoopStorageWrapperTest.testStore.close();
            fs.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}