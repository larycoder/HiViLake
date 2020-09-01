package com.usth.hieplnc.storage.api;

/**
 * Doc:
 * The general interface for hivilake storage
 * 
 */

import java.util.List;

public interface StorageWrapper{
    public void setParam(String key, String value);
    public List<String> support();
    public void close();
}
