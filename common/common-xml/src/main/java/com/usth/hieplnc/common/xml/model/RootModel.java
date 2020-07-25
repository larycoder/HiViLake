package com.usth.hieplnc.common.xml.model;

import javax.xml.bind.annotation.XmlElement;

public class RootModel{
    private String name;
    private String value;

    @XmlElement(name = "name")
    public void setName(String name){ this.name = name; }
    public String getName(){ return name; }

    @XmlElement(name = "value")
    public void setValue(String value){ this.value = value; }
    public String getValue(){ return value; }

    public void display(){
        System.out.println("Element \"Root\": ");
        System.out.println("Name: " + getName());
        System.out.println("Value: " + getValue());
    }
}
