package com.usth.hieplnc.common.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.util.HashMap;
import java.nio.file.Paths;

import java.io.IOException;
import java.lang.RuntimeException;

import com.usth.hieplnc.common.hadoop.FileFinder;

public class Storage{
// variable
    
    private FileSystem local = null;
    private FileSystem hadoop = null;
    
    private FileFinder finder = null;
    
    private String inputDir = null;
    private String outputDir = null;
    
    public boolean delSrc = true;

//=================================================================//
// constructor

    public Storage(FileSystem local, FileSystem hadoop){
        this.local = local;
        this.hadoop = hadoop;
        finder = new FileFinder(local);
    }

    public Storage(HashMap<String, String> conf, String local, String hadoop) throws IOException{
        Configuration configuration = loadConf(conf);
        this.local = (new Path(local)).getFileSystem(configuration);
        this.hadoop = (new Path(hadoop)).getFileSystem(configuration);
        finder = new FileFinder(this.local);
    }

    public Storage() throws IOException{ // factory constructor
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        
        local = (new Path("file://")).getFileSystem(conf);
        hadoop = (new Path("hdfs://")).getFileSystem(conf);
        finder = new FileFinder(local);
    }

//=================================================================//
// method

    private Configuration loadConf(HashMap<String, String> conf){
        Configuration newConf = new Configuration();
        for(String key : conf.keySet()){
            newConf.set(key, conf.get(key));
        }
        return newConf;
    }

    public FileSystem getLocalFS(){ return local; }
    public FileSystem getHadoopFS(){ return hadoop; }

    public void close() throws IOException{
        if(local != null) local.close();
        if(hadoop != null) hadoop.close();
    }

    private boolean isHiviDir(FileStatus[] dir){
        for(FileStatus status : dir){
            if(status.getPath().getName().equals(".hivilake") && status.isDirectory()){
                return true;
            }
        }
        return false;
    }

    public void setInputDir(String input) throws IOException{
        Path inputPath = new Path(input);

        if(! local.getFileStatus(inputPath).isDirectory()){
            throw new IOException("Input Path is not directory");
        } else if(! isHiviDir(local.listStatus(inputPath))){
            throw new IOException("Input Path " + input + " is not hivilake directory");
        }

        inputDir = input;
    }

    public void setOutputDir(String output) throws IOException{
        Path outputPath = new Path(output);

        if(! hadoop.getFileStatus(outputPath).isDirectory()){
            throw new IOException("Input Path is not directory");
        } else if(! isHiviDir(hadoop.listStatus(outputPath))){
            throw new IOException("Input Path " + output + " is not hivilake directory");
        }

        outputDir = output;
    }

    public String getInputDir(){ return inputDir; }
    public String getOutputDir(){ return outputDir; }

    private Path getAbsInput(Path file){
        return new Path(Paths.get(inputDir, file.toString()).toString());
    }

    private Path getAbsOutput(Path file){
        return new Path(Paths.get(outputDir, file.toString()).toString());
    }

    private void transferMethod(Path input, Path output) throws IOException{
        hadoop.copyFromLocalFile(delSrc, input, output);
    }

    public void pushFile(Path input, Path output, String opt) throws IOException{
        input = getAbsInput(input);
        output = getAbsOutput(output);
        finder.setPattern(input.toString());
        FileStatus status;

        while(finder.next()){
            status = finder.getFile();
            if(opt.equals("file")){
                if(status.isFile()){
                    transferMethod(status.getPath(), output);
                }
                continue;
            } else if(opt.equals("dir")){
                if(status.isDirectory()){
                    transferMethod(status.getPath(), output);
                }
                continue;
            } else if(opt.equals("both")){
                transferMethod(status.getPath(), output);
                continue;
            } else{
                throw new RuntimeException("Can not decide action for path: " + status.getPath().toString());
            }
        }
    }

    public static void main( String[] args ) throws IOException{
        Storage myStore = new Storage();
        myStore.setInputDir("/tmp/hieplnc/hivilake/input");
        System.out.println(myStore.getInputDir());
    }

}
