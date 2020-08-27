package com.usth.hieplnc.storage.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.util.HashMap;
import java.nio.file.Paths;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.lang.RuntimeException;
import java.lang.NullPointerException;

import com.usth.hieplnc.storage.hadoop.FileFinder;
import com.usth.hieplnc.storage.hadoop.FinderResult;

public class Storage{
// variable
    
    private FileSystem local = null;
    private FileSystem hadoop = null;
    
    private FileFinder finder = null;
    
    private String inputDir = null;
    private String outputDir = null;

//=================================================================//
// constructor

    public Storage(FileSystem local, FileSystem hadoop){
        this.local = local;
        this.hadoop = hadoop;
    }

    public Storage(HashMap<String, String> conf, String local, String hadoop) throws IOException{
        Configuration configuration = loadConf(conf);
        this.local = (new Path(local)).getFileSystem(configuration);
        this.hadoop = (new Path(hadoop)).getFileSystem(configuration);
    }

    public Storage() throws IOException{ // factory constructor
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");
        
        local = (new Path("file://")).getFileSystem(conf);
        hadoop = (new Path("hdfs://")).getFileSystem(conf);
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
        if(inputDir == null) throw new NullPointerException("Input Root is not setted");
        return new Path(Paths.get(inputDir, file.toString()).toString());
    }

    private Path getAbsOutput(Path file){
        if(outputDir == null) throw new NullPointerException("Input Root is not setted");
        return new Path(Paths.get(outputDir, file.toString()).toString());
    }

    public String getAbsInput(String file){ return getAbsInput(new Path(file)).toString(); }
    public String getAbsOutput(String file){ return getAbsOutput(new Path(file)).toString(); }

    public void copyFromLocalFile(boolean delSrc, String input, String output) throws IOException{
        hadoop.copyFromLocalFile(delSrc, new Path(input), new Path(output));
    }

    private boolean isLocal(String location){
        if(location.equals("local")){
            return true;
        } else if(location.equals("hadoop")){
            return false;
        } else{
            throw new RuntimeException("Can not active file finder with option " + location);
        }
    }

    public void activeFileFinder(String location){
        if(location.equals("local")){
            finder = new FileFinder(local);
        } else if(location.equals("hadoop")){
            finder = new FileFinder(hadoop);
        } else{
            throw new RuntimeException("Can not active file finder with option " + location);
        }
    }

    public FinderResult search(String pattern) throws FileNotFoundException, IOException{
        if(finder == null) throw new NullPointerException("File Finder is not active");
        finder.setPattern(pattern);
        return new FileWrapper(finder);
    }

    public InputStream open(String path, String location) throws RuntimeException, IOException{
        if(isLocal(location)){
            return local.open(new Path(path));
        } else{
            return hadoop.open(new Path(path));
        }
    }

    public static void main( String[] args ) throws IOException, NullPointerException{
        Storage myStore = new Storage();
        myStore.setInputDir("/tmp/hieplnc/hivilake/input");
        myStore.setOutputDir("/user/root/hivilake/output");
        
        FileWrapper dir = new FileWrapper(myStore.getLocalFS().getFileStatus(new Path("/bin")), myStore.getLocalFS());
        String[] listFile = dir.listStringFile();
        for(String i : listFile){
            System.out.println(i);
        }

        // close  storage
        myStore.close();
    }

}
