package com.usth.hieplnc.common.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class DatabaseModel{
    private String name;
    private List<TableModel> table;

    @XmlElement
    public void setName(String name){ this.name = name; }
    public String getName(){ return name; }

    @XmlElementWrapper(name = "tables")
    @XmlElement
    public void setTable(List<TableModel> table){ this.table = table; }
    public List<TableModel> getTable(){ return table; }
}