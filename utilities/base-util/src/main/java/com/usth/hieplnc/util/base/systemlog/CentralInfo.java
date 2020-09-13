package com.usth.hieplnc.util.base.systemlog;

/**
 * DOC:
 * - CentralInfo provides a method to interact with system global information
 *
 */

import java.util.List;

public interface CentralInfo{
    public void registerUser(String name, String desc);
    public List<String> listUser();
    public String getUserInfo(String name);

    public void registerCatalog(String name, String desc);
    public List<String> listCatalog();
    public String getCatalogInfo(String name);

    public void setupTime(String startTime, String location);
    public String getCurrent();
}