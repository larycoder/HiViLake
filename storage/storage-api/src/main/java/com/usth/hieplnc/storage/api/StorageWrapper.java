package com.usth.hieplnc.storage.api;

/**
 * Doc:
 * The general interface for hivilake storage
 * 
 */

import java.util.List;
import java.io.IOException;

public interface StorageWrapper{
    public void setParam(String key, String value);
    public List<String> support();
    public void close() throws IOException;
}
