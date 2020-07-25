package com.usth.hieplnc.common.xml.model;

import javax.xml.bind.annotation.XmlElement;

public class ColumnModel{
    private String name;
    private String type;

    @XmlElement
    public void setName(String name){ this.name = name; }
    public String getName(){ return name; }

    @XmlElement
    public void setType(String type){ this.type = type; }
    public String getType(){ return type; }

    public void display(){
        System.out.println("Element \"Column\":");
        System.out.println("Name: " + getName());
        System.out.println("Type: " + getType());
    }
}