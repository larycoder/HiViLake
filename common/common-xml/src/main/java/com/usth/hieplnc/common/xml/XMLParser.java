package com.usth.hieplnc.common.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javax.xml.bind.JAXBException;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import java.lang.NullPointerException;

import com.usth.hieplnc.common.xml.model.*;

public class XMLParser{
// variable

    private InputStream location = null;

//==================================================================//
// constructor

    public XMLParser(InputStream location){
        this.location = location;
    }

//==================================================================//
// method

    public <E> E parse(Class<E> wrapperClass){
        try{
            return (E) JAXBContext.newInstance(wrapperClass).createUnmarshaller().unmarshal(location);
        } catch(JAXBException e){
            e.printStackTrace();
            throw new NullPointerException("Can not parse XML to " + wrapperClass.getName());
        }
    }

    public static void main(String[] args) throws JAXBException, FileNotFoundException{

        /*
            * test path
            *
        */
        XMLParser myParser = new XMLParser(new FileInputStream(new File("/tmp/hieplnc/hivilake/input/.hivilake/SOF.xml")));
        SchemaModel myIOF = myParser.parse(SchemaModel.class);
    }
}
