package com.usth.hieplnc.storage.api.filesystem;

/**
 * Doc:
 * - The interface represents file
 * 
 */

import java.io.InputStream;
import java.io.OutputStream;

public interface SWFile{
    public InputStream readStream();
    public void append(InputStream streamData);
    public OutputStream writeStream();
}
