package com.usth.hieplnc.common.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class TableModel{
    private String name;
    private List<ColumnModel> column;

    @XmlElement
    public void setName(String name){ this.name = name; }
    public String getName(){ return name; }

    @XmlElementWrapper(name = "columns")
    @XmlElement
    public void setColumn(List<ColumnModel> column){ this.column = column; }
    public List<ColumnModel> getColumn(){ return column; }
}