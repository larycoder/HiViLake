package com.usth.hieplnc.storage.api.filesystem;

/**
 * Doc:
 * - The interface represents path information
 * 
 */

import java.io.IOException;

import com.usth.hieplnc.storage.api.filesystem.SWListPath;

public interface SWFilestatus{
    public boolean isFile();
    public boolean isDir();
    public String getPath();
}