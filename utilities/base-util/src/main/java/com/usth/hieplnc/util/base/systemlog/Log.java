package com.usth.hieplnc.util.base.systemlog;

/**
 * DOC:
 * - Log interface for logging
 *
 */

import org.json.simple.JSONObject;

import com.usth.hieplnc.util.base.systemlog.model.*;

public interface Log{
    public void trackActivity(ActivityLogModel log);
    public Integer countActivity();
    public JSONObject listActivity();

    public void addRepo(RepoLogModel log);
    public void updateRepo(int repoId, JSONObject data);
    public String getRepoLocation(int repoId);
    public JSONObject listRepo();
}