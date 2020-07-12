package com.usth.hieplnc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.*;

import org.apache.hadoop.fs.*;

public class RegexFile implements PathFilter{
// variable

    FileSystem fs = null;
    String pattern = null;

    ArrayList<Pattern> engine = null;

//=========================================================================//
// constructor

    public RegexFile(FileSystem fs){ this.fs = fs; }

//=========================================================================//
// method
    
    public void setRegexPath(String regexPath){ 
        pattern = regexPath;
        setRegexEngine();
    }

    public String getRegexPath(){ return pattern; }

    private static ArrayList<String> parseRegexPath(String pattern){
        ArrayList<String> pathName = new ArrayList<>();
        Path path = new Path(pattern);
        pathName.add(path.getName());
        while((path = path.getParent()) != null){
            pathName.add(path.getName());
        }
        // reverse list
        Collections.reverse(pathName);
        return pathName;
    }

    private void setRegexEngine(){
        ArrayList<Pattern> engine = new ArrayList<>();
        ArrayList<String> listPath = parseRegexPath(this.pattern);
        for(String i : listPath){
            Pattern pathPattern = Pattern.compile(i);
            engine.add(pathPattern);
        }
        this.engine = engine;
    }

    @Override
    public boolean accept(Path path){
        ArrayList<String> realPath = parseRegexPath(path.toString());

        if(engine.size() < realPath.size()) return false;

        for(int i = 0; i < realPath.size(); i++){
            if(!engine.get(i).matcher(realPath.get(i)).find()) return false;
        }
        return true;
    }

    public static void main(String[] args){
        System.out.println("hello world");
    }
}