package com.usth.hieplnc;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import java.net.*;
import java.nio.file.Paths;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

public class BasicIngest{
// Variable

    Configuration conf = null;

    FileSystem lfs = null;
    FileSystem hafs = null;

    String inputDir = null;
    String outputDir = null;

// =========================================================================================== //
// Constructor

    public BasicIngest(Configuration conf){
        this.conf = new Configuration(conf);
    }

    // factory constructor
    public BasicIngest(){
        // Set configuration for connecting
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        this.conf = new Configuration(conf);
    }

// ============================================================================================ //
// Method

    public void setFS(String lfs, String hafs) throws IOException{
        if(lfs != null){
            if(this.lfs != null) this.lfs.close();
            this.lfs = (new Path(lfs)).getFileSystem(conf);
        }
        if(hafs != null){
            if(this.hafs != null) this.hafs.close();
            this.hafs = (new Path(hafs)).getFileSystem(conf);
        }
    }

    public FileSystem getFS(String type){
        if(type == "local") return this.lfs;
        else if(type == "hadoop") return this.hafs;
        return null;
    }

    public void closeFS() throws IOException{
        if(lfs != null) lfs.close();
        if(hafs != null) hafs.close();
    }

    public void setConf(Configuration conf){
        this.conf = conf;
    }

    public Configuration getConf(){
        return conf;
    }

    public void setInputDir(String input) throws IOException{
        if(lfs.exists(new Path(input))){
            this.inputDir = input;
            return;
        }
        throw new FileNotFoundException("Path file is not exists in input file system");
    }

    public String getInputDir(){
        return this.inputDir;
    }

    public void setOutputDir(String output) throws IOException{
        if(hafs.exists(new Path(output))){
            this.outputDir = output;
            return;
        }
        throw new FileNotFoundException("Path file is not exists in output file system");
    }

    public String getOutputDir(){
        return this.outputDir;
    }

    public Path getAbsInput(Path file){
        return new Path(Paths.get(this.inputDir.toString(), file.toString()).toString());
    }

    public Path getAbsOutput(Path file){
        return new Path(Paths.get(this.outputDir.toString(), file.toString()).toString());
    }

    public void pushFile(Path input, Path output, boolean delSrc) throws IOException{
        Path finalInput = getAbsInput(input);
        Path finalOutput = getAbsOutput(output);
        hafs.copyFromLocalFile(delSrc, finalInput, finalOutput);
    }

    public void pushMultiFile(Path input, Path output, boolean delSrc) throws IOException{
        FileStatus inputStatus = lfs.getFileStatus(getAbsInput(input));
        FileStatus outputStatus = hafs.getFileStatus(getAbsOutput(output));

        // check directory
        if (!inputStatus.isDirectory() || !outputStatus.isDirectory()){
            throw new IOException("Input or Output Path is not directory");
        }

        // get list of file
        FileStatus[] listInputStatus = lfs.listStatus(getAbsInput(input));

        List<Path> listInputPath = new ArrayList<Path>();
        Path outputPath = getAbsOutput(output);

        for(FileStatus status : listInputStatus){
            if(status.isFile()) listInputPath.add(status.getPath());
        }

        // push file to hdfs
        Path[] arrayInputPath = new Path[listInputPath.size()];
        arrayInputPath = listInputPath.toArray(arrayInputPath);
        hafs.copyFromLocalFile(delSrc, false, listInputPath.toArray(arrayIn), outputPath);
    }

    public static void main(String[] args) throws IOException{
        BasicIngest bi = new BasicIngest();
        bi.setFS("file://", "hdfs://");
        bi.setInputDir("/tmp/hieplnc/hivilake/input");
        bi.setOutputDir("/user/root/hivilake/output");
        
        // List Path
        System.out.println("List file:");
        FileSystem fs = bi.getFS("local");
        FileStatus[] fileStatus = fs.listStatus(new Path(bi.getInputDir()));
        for(FileStatus status : fileStatus){
            System.out.println(status.getPath().toString());
        }

        // Copy local file to hdfs
        // bi.pushFile(new Path("./test.txt"), new Path("./"), false);
        bi.pushMultiFile(new Path("./"), new Path("./"), false);

        // close filesystem
        bi.closeFS();
    }
}