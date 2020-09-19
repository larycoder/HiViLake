package com.usth.hieplnc.util.api;

/**
 * DOC:
 * - This interface is the socket to connect service with API layer
 *
 */

import java.io.InputStream;
import java.io.OutputStream;

import org.json.simple.JSONObject;

public interface Service{
    public boolean setParameter(JSONObject param);
    public JSONObject getStatus();
    public InputStream pullFile();
    public void pushFile(InputStream data);
    public OutputStream pushFile();
}
