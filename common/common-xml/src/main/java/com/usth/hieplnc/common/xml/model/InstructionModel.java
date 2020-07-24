package com.usth.hieplnc.common.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement(name = "files")
public class InstructionModel{
    private List<Root> root;
    private List<Path> path;

    @XmlElementWrapper(name = "roots")
    @XmlElement(name = "root")
    public void setRoot(List<Root> root){ this.root = root; }
    public List<Root> getRoot(){ return root; }

    @XmlElementWrapper(name = "paths")
    @XmlElement(name = "path")
    public void setPath(List<Path> path){ this.path = path; }
    public List<Path> getPath(){ return path; }
}
