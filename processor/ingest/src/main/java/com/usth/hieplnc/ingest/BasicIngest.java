package com.usth.hieplnc.ingest;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.lang.RuntimeException;
import java.lang.NullPointerException;

import java.util.ArrayList;
import java.util.List;

import java.net.*;
import java.nio.file.Paths;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import com.usth.hieplnc.common.xml.XMLParser;
import com.usth.hieplnc.common.xml.model.*;

import com.usth.hieplnc.ingest.FileFinder;

public class BasicIngest{
// Variable

    Configuration conf = null;

    FileSystem lfs = null;
    FileSystem hafs = null;

    FileFinder finder = null;

    String inputDir = null;
    String outputDir = null;

    public boolean delSrc;

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
            this.finder = new FileFinder(this.lfs);
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
        Path inputPath = new Path(input);
        if(lfs.exists(inputPath)){
            if(!lfs.getFileStatus(inputPath).isDirectory()){
                throw new IOException("Input Path is not directory");
            } else {
                FileStatus[] fileStatus = lfs.listStatus(inputPath);
                int has = 0;
                for(FileStatus status : fileStatus){
                    if(status.getPath().getName() != ".hivilake" && status.isDirectory()){
                        has = 1;
                        break;
                    }
                }
                if(has == 0) throw new IOException("Input Path " + input + " is not hivilake Dir");
            }
            this.inputDir = input;
            return;
        }
        throw new FileNotFoundException("Path file is not exists in input file system");
    }

    public String getInputDir(){
        return this.inputDir;
    }

    public void setOutputDir(String output) throws IOException{
        Path outputPath = new Path(output);
        if(hafs.exists(outputPath)){
            if(!hafs.getFileStatus(outputPath).isDirectory()){
                throw new IOException("Output Path is not directory");
            } else {
                FileStatus[] fileStatus = hafs.listStatus(outputPath);
                int has = 0;
                for(FileStatus status : fileStatus){
                    if(status.getPath().getName() != ".hivilake" && status.isDirectory()){
                        has = 1;
                        break;
                    }
                }
                if(has == 0) throw new IOException("Output Path " + output + " is not hivilake Dir");
            }
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

    public void pushFile(Path input, Path output, boolean delSrc, String option) throws IOException{
        output = getAbsOutput(output);
        input = getAbsInput(input);
        finder.setPattern(input.toString());
        FileStatus inputPath;

        while(finder.next()){
            inputPath = finder.getFile();
            if(option.equals("file")){
                if(inputPath.isFile()){
                    hafs.copyFromLocalFile(delSrc, inputPath.getPath(), output);
                } else{
                    continue;
                }
            } else if(option.equals("dir")){
                if(inputPath.isDirectory()){
                    hafs.copyFromLocalFile(delSrc, inputPath.getPath(), output);
                } else{
                    continue;
                }
            } else if(option.equals("both")){
                hafs.copyFromLocalFile(delSrc, inputPath.getPath(), output);
            } else{
                throw new IOException("Can not decide action for path: " + inputPath.getPath().toString());
            }
        }
    }

    protected InstructionModel loadXML(String path){
        // Parse XML
        // Method Variable
        FSDataInputStream in = null;
        InstructionModel result = null;

        try{
            // load XML file
            in = getFS("local").open(getAbsInput(new Path(path)));
            
            // parse xml data to model object
            XMLParser xmlParser = new XMLParser(in);
            result = xmlParser.parse(InstructionModel.class);
        } catch(Exception e){
            e.printStackTrace();
            result = null;
        } finally{
            try{
                in.close();
            } catch(IOException e){
                e.printStackTrace();
            }
            return result;
        }
    }

    public void ingestFile() throws IOException{
        InstructionModel files = loadXML(".hivilake/IOF.xml");

        // get root of IO ingest
        String inputRoot = null;
        String outputRoot = null;

        for(RootModel root : files.getRoot()){
            if(root.getName().equals("input")){
                inputRoot = root.getValue();
            } else if(root.getName().equals("output")){
                outputRoot = root.getValue();
            }
        }

        // following instruction
        for(PathModel path : files.getPath()){
            Path inputPath = new Path(Paths.get(inputRoot, path.getInput()).toString()); // set input path
            Path outputPath = new Path(Paths.get(outputRoot, path.getOutput()).toString()); // set output path
            String option = path.getType() == null ? "both" : path.getType(); // set type
            pushFile(inputPath, outputPath, this.delSrc, option); // push file to hadoop
        }
    }

    public static void main(String[] args) throws IOException, NullPointerException{
        BasicIngest bi = new BasicIngest();
        bi.setFS("file://", "hdfs://");
        bi.setInputDir("/tmp/hieplnc/hivilake/input");
        bi.setOutputDir("/user/root/hivilake/output");

        // set argument from outside
        for(int i = 0; i < args.length; i++){
            String[] parameter = args[i].split("=");

            //set parameter
            if(parameter[0].equals("--inputDir")) bi.setInputDir(parameter[1]);
            else if(parameter[0].equals("--outputDir")) bi.setOutputDir(parameter[1]);
            else if(parameter[0].equals("--delSrc")) bi.delSrc = Boolean.parseBoolean(parameter[1]);

            // execute parameter
            else if(parameter[0].equals("ingest")){
                // push file to hadoop
                bi.ingestFile();
            }

            else throw new RuntimeException("Invalid Arguments");
        }

        //====================================================================================================//
        // Test Path
        
        // List Path
        // System.out.println("List file:");
        // FileSystem fs = bi.getFS("local");
        // FileStatus[] fileStatus = fs.listStatus(new Path(bi.getInputDir()));
        // for(FileStatus status : fileStatus){
        //     System.out.println(status.getPath().toString());
        // }

        // Copy local file to hdfs
        // bi.pushOneFile(new Path("./lh-mvn"), new Path("./"), false);
        // bi.pushMultiFile(new Path("./"), new Path("./"), false);

        // Automatic ingest file following IOF file
        // bi.ingestFile();

        // test regex filter
        // FileSystem testFs = bi.getFS("local");
        // RegexFile testPattern = new RegexFile(testFs);
        // testPattern.dirOnly = true;
        // testPattern.setRegexPath(bi.getAbsInput(new Path("./test*")).toString());
        // FileStatus[] listStatus = testFs.listStatus(bi.getAbsInput(new Path("./")), testPattern);
        // for(FileStatus i : listStatus){
        //     System.out.println(i.getPath().toString());
        // }

        // test explore directory
        // FileSystem testFs = bi.getFS("local");
        // FileFinder myFinder = new FileFinder(testFs);
        // myFinder.setPattern(bi.getAbsInput(new Path(".*/.*")).toString());
        // while(myFinder.next()){
        //     System.out.println(myFinder.getFile().getPath().toString());
        // }
        
        // test copy to hadoop
        // FileSystem myFS = bi.getFS("hadoop");
        // myFS.copyFromLocalFile(false, bi.getAbsInput(new Path("test_path")), bi.getAbsOutput(new Path("./ee")));

        // test new xml mechanism
        XMLParser myParser = new XMLParser(bi.getFS("local").open(new Path("/tmp/hieplnc/hivilake/input/.hivilake/IOF.xml")));
        InstructionModel myIOF = myParser.parse(InstructionModel.class);
        for(PathModel i : myIOF.getPath()){
            i.display();
        }

        //======================================================================================================//

        // close filesystem
        bi.closeFS();
    }
}