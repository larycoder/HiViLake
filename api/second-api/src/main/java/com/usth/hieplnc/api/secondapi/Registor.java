package com.usth.hieplnc.api.secondapi;

/**
 * DOC:
 * - This interface provide the api for connect util service with export server
 *
 */

import org.json.simple.JSONObject;

import com.usth.hieplnc.util.api.Service;

public interface Registor{
    public void register(Service service, JSONObject api);
}
