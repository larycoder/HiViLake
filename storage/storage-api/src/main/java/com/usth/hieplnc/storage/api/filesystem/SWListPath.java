package com.usth.hieplnc.storage.api.filesystem;

/**
 * Doc:
 * - The interface represents list of path status
 * 
 */

import com.usth.hieplnc.storage.api.filesystem.SWFilestatus;

public interface SWListPath{
    public SWFilestatus get(int index);
    public int size();
}