package com.usth.hieplnc.ingest;

import com.usth.hieplnc.ingest.RegexFile;

import org.apache.hadoop.fs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileFinder{
// variable

    RegexFile regexPattern;
    FileSystem fs;
    ArrayList<ArrayList<FileStatus>> pathStack;
    ArrayList<FileStatus> lastStack;

//===========================================================//
// constructor

    public FileFinder(FileSystem fs, RegexFile regexPattern){
        this.regexPattern = regexPattern;
        this.fs = fs;
    }

    public FileFinder(FileSystem fs){ // factory constructor
        regexPattern = new RegexFile(fs);
        this.fs = fs;
    }

//===========================================================//
// method

    private FileStatus popStack(ArrayList<FileStatus> listPath){
        if(listPath.isEmpty()) return null;

        int lastIndex = listPath.size() - 1;
        FileStatus path = listPath.get(lastIndex);
        listPath.remove(lastIndex);
        return path;
    }

    private ArrayList<FileStatus> getNewPath(int depth) throws FileNotFoundException, IOException{
        ArrayList<FileStatus> listPath = pathStack.get(depth);
        String saveState = getRegexFilter();
        while(true){
            if(listPath.isEmpty()){
                if(depth == 0){
                    return null;
                }
                else{
                    setRegexFilter("dir");
                    ArrayList<FileStatus> tempArray = getNewPath(depth - 1);
                    if(tempArray == null) return null;
                    listPath.addAll(tempArray);
                }
            }

            FileStatus pathStatus = popStack(listPath);
            setRegexFilter(saveState);
            FileStatus[] nextArrayPath = fs.listStatus(pathStatus.getPath(), regexPattern);
            if(nextArrayPath != null){
                if(nextArrayPath.length != 0){
                    return new ArrayList<>(Arrays.asList(nextArrayPath));
                }
            }
        }
    }

    private void setRegexFilter(String status){
        switch(status){
            case "both":
                regexPattern.dirOnly = false;
                regexPattern.fileOnly = false;
                break;
            case "file":
                regexPattern.dirOnly = false;
                regexPattern.fileOnly = true;
                break;
            case "dir":
                regexPattern.dirOnly = true;
                regexPattern.fileOnly = false;
                break;
            case "nothing":
                regexPattern.dirOnly = true;
                regexPattern.fileOnly = true;
                break;
            default:
                ;
        }
    }

    private String getRegexFilter(){
        if(regexPattern.fileOnly == false && regexPattern.dirOnly == false) return "both";
        else if(regexPattern.fileOnly == true && regexPattern.dirOnly == false) return "file";
        else if(regexPattern.fileOnly == false && regexPattern.dirOnly == true) return "dir";
        else if(regexPattern.fileOnly == true && regexPattern.dirOnly == true) return "nothing";
        return null;
    }

    private void initializeStack() throws FileNotFoundException, IOException{
        ArrayList<FileStatus> firstStack = pathStack.get(0);
        setRegexFilter("both");
        FileStatus[] rootPath = fs.listStatus(new Path("/"), regexPattern);
        firstStack.addAll(new ArrayList<>(Arrays.asList(rootPath)));
    }

    public FileStatus getFile(){
        if(lastStack.isEmpty()) return null;
        return popStack(lastStack);
    }

    public boolean next() throws FileNotFoundException, IOException{
        if(lastStack.isEmpty()){
            int depth = pathStack.size() - 1;
            if(depth == 0) return false;
            setRegexFilter("both");
            ArrayList<FileStatus> listPath = getNewPath(depth - 1);
            if(listPath == null) return false;
            lastStack.addAll(listPath);
        }
        return true;
    }

    public void setPattern(String pattern) throws FileNotFoundException, IOException{
        regexPattern.setRegexPath(pattern);

        int pathLength = (new Path(pattern)).depth();
        try{
            pathStack.clear();
        } catch(NullPointerException e){
            pathStack = new ArrayList<>();
        }
        for(int i = 0; i < pathLength; i++){
            pathStack.add(new ArrayList<FileStatus>());
        }
        lastStack = pathStack.get(pathLength - 1);
        
        initializeStack();
    }
}