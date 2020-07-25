package com.usth.hieplnc.common.xml.model;

import javax.xml.bind.annotation.XmlElement;

public class PathModel{
    private String input;
    private String output;
    private String type;

    @XmlElement
    public void setInput(String input){ this.input = input; }
    public String getInput(){ return input; }

    @XmlElement
    public void setOutput(String output){ this.output = output; }
    public String getOutput(){ return output; }

    @XmlElement
    public void setType(String type){ this.type = type; }
    public String getType(){ return type;}

    public void display(){
        System.out.println("Element \"Path\": ");
        System.out.println("Input: " + getInput());
        System.out.println("Output: " + getOutput());
        System.out.println("Type: " + getType());
    }
}
