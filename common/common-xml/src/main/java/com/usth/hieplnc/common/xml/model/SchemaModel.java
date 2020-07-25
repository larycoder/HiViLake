package com.usth.hieplnc.common.xml.model;

import javax.xml.bind.annotation.XmlElement;  
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement(name = "schema")
public class SchemaModel{
    private String name;
    private DatabaseModel database;

    @XmlElement
    public void setName(String name){ this.name = name; }
    public String getName(){ return name; }

    @XmlElement
    public void setDatabase(DatabaseModel database){ this.database = database; }
    public DatabaseModel getDatabase(){ return database; }
}