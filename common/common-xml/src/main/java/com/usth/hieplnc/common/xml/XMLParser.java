package com.usth.hieplnc.common.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javax.xml.bind.JAXBException;

import java.io.InputStream;
import java.io.File;

import com.usth.hieplnc.common.xml.model.*;

public class XMLParser{
// variable

    private InputStream location = null;

//==================================================================//
// constructor

    public XMLParser(InputStream location){
        this.location = location;
    }

    public XMLParser(){ super(); }

//==================================================================//
// method

    public <E> E parse(Class<E> wrapperClass) throws JAXBException{
        return (E) JAXBContext.newInstance(wrapperClass).createUnmarshaller().unmarshal(new File("/tmp/hieplnc/hivilake/input/.hivilake/IOF.xml"));
    }

    public static void main(String[] args) throws JAXBException{

        /*
            * test path
            *
        */
        XMLParser myParser = new XMLParser();
        InstructionModel myIOF = myParser.parse(InstructionModel.class);
        for(Path i : myIOF.getPath()){
            i.display();
        }
    }
}
