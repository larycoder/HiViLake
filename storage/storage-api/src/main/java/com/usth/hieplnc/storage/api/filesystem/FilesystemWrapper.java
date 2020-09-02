package com.usth.hieplnc.storage.api.filesystem;

/**
 * Doc:
 * - The interface is represent of filesystem
 * - It extend the StorageWrapper interface
 * 
 */

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.api.filesystem.model.*;
import com.usth.hieplnc.storage.api.filesystem.SWListPath;
import com.usth.hieplnc.storage.api.filesystem.SWFile;
import com.usth.hieplnc.storage.api.filesystem.SWFilestatus;

public interface FilesystemWrapper extends StorageWrapper{
    public void createPath(String path, PathType type);
    public void deletePath(String path, SWOption option);
    public SWFile openFile(String path);
    public SWFilestatus getStatus(String path);
    public Iterator<SWFilestatus> searchFile(JSONObject pattern);
}
