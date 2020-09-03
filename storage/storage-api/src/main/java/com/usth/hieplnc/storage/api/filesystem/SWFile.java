package com.usth.hieplnc.storage.api.filesystem;

/**
 * Doc:
 * - The interface represents file
 * 
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public interface SWFile{
    public InputStream readStream() throws IOException;
    public void append(InputStream streamData) throws IOException;
    public void writeStream(InputStream streamData) throws IOException;
}
