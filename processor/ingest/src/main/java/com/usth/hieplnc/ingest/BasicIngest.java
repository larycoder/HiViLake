package com.usth.hieplnc.ingest;

import java.io.IOException;
import java.io.InputStream;

import java.lang.RuntimeException;
import java.lang.NullPointerException;

// import java.util.ArrayList;
// import java.util.List;

// import java.net.*;
import java.nio.file.Paths;

import com.usth.hieplnc.common.xml.XMLParser;
import com.usth.hieplnc.common.xml.model.*;

import com.usth.hieplnc.common.hadoop.Storage;
import com.usth.hieplnc.common.hadoop.FileWrapper;
import com.usth.hieplnc.common.hadoop.FinderResult;

public class BasicIngest{
// Variable

    private Storage storage = null;
    public boolean delSrc = true;

// =========================================================================================== //
// Constructor

    public BasicIngest(Storage storage){ this.storage = storage; }

    // factory constructor
    public BasicIngest() throws IOException{
        storage = new Storage();
    }

// ============================================================================================ //
// Method

    public void pushFile(String input, String output, boolean delSrc, String option) throws IOException{
        String pattern = storage.getAbsInput(input);
        output = storage.getAbsOutput(output);
        FileWrapper path;

        FinderResult finderResult = storage.search(pattern);

        while(finderResult.next()){
            path = finderResult.getFile();
            if(option.equals("file")){
                if(path.isFile()){
                    storage.copyFromLocalFile(delSrc, path.getPath().toString(), output);
                }
                continue;
            } else if(option.equals("dir")){
                if(path.isDirectory()){
                    storage.copyFromLocalFile(delSrc, path.getPath().toString(), output);
                }
                continue;
            } else if(option.equals("both")){
                storage.copyFromLocalFile(delSrc, path.getPath().toString(), output);
                continue;
            } else{
                throw new IOException("Can not decide action for path: " + input);
            }
        }
    }

    protected InstructionModel loadXML(String path){
        // Parse XML
        // Method Variable
        InputStream in = null;
        InstructionModel result = null;

        try{
            // load XML file
            in = storage.open(storage.getAbsInput(path), "local");
            
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

        // active file finder
        storage.activeFileFinder("local");

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
            String inputPath = Paths.get(inputRoot, path.getInput()).toString(); // set input path
            String outputPath = Paths.get(outputRoot, path.getOutput()).toString(); // set output path
            String option = path.getType() == null ? "both" : path.getType(); // set type
            pushFile(inputPath, outputPath, this.delSrc, option); // push file to hadoop
        }
    }

    public Storage getStorage(){ return storage; }

    public void close() throws IOException{ storage.close(); }

    public static void main(String[] args) throws IOException, NullPointerException{
        BasicIngest bi = new BasicIngest();
        bi.getStorage().setInputDir("/tmp/hieplnc/hivilake/input");
        bi.getStorage().setOutputDir("/user/root/hivilake/output");
        bi.delSrc = false;

        // set argument from outside
        for(int i = 0; i < args.length; i++){
            String[] parameter = args[i].split("=");

            //set parameter
            if(parameter[0].equals("--inputDir")) bi.getStorage().setInputDir(parameter[1]);
            else if(parameter[0].equals("--outputDir")) bi.getStorage().setOutputDir(parameter[1]);
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
        // XMLParser myParser = new XMLParser(bi.getFS("local").open(new Path("/tmp/hieplnc/hivilake/input/.hivilake/IOF.xml")));
        // InstructionModel myIOF = myParser.parse(InstructionModel.class);
        // for(PathModel i : myIOF.getPath()){
        //     i.display();
        // }

        //======================================================================================================//

        // close filesystem
        bi.close();
    }
}