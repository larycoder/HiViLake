package com.usth.hieplnc.common.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement(name = "files")
public class InstructionModel{
    private List<RootModel> root;
    private List<PathModel> path;

    @XmlElementWrapper(name = "roots")
    @XmlElement(name = "root")
    public void setRoot(List<RootModel> root){ this.root = root; }
    public List<RootModel> getRoot(){ return root; }

    @XmlElementWrapper(name = "paths")
    @XmlElement(name = "path")
    public void setPath(List<PathModel> path){ this.path = path; }
    public List<PathModel> getPath(){ return path; }
}
