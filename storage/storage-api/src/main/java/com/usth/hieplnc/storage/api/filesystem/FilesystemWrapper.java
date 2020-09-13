package com.usth.hieplnc.storage.api.filesystem;

/**
 * Doc:
 * - The interface is represent of filesystem
 * - It extend the StorageWrapper interface
 * 
 */

import java.util.Iterator;
import java.io.IOException;

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.StorageWrapper;
import com.usth.hieplnc.storage.api.filesystem.model.*;
import com.usth.hieplnc.storage.api.filesystem.SWListPath;
import com.usth.hieplnc.storage.api.filesystem.SWFile;
import com.usth.hieplnc.storage.api.filesystem.SWFilestatus;

public interface FilesystemWrapper extends StorageWrapper{
    public void createPath(String path, int type) throws IOException;
    public void deletePath(String path, int option) throws IOException;
    public SWFile openFile(String path) throws IOException;
    public boolean exists(String path) throws IOException;
    public SWFilestatus getStatus(String path) throws IOException;
    public Iterator<SWFilestatus> searchFile(JSONObject pattern) throws IOException;
    public SWListPath listStatus(SWFilestatus fstatus) throws IOException;
}
