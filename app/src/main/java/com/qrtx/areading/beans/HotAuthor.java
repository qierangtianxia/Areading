package com.qrtx.areading.beans;

import java.io.Serializable;

/**
 * Created by user on 17-3-15.
 */

public class HotAuthor implements Serializable {
    private String id;
    private String name;
    private String resume;//简介
    private String alt;//个性签名
    private String persionPagerUrl;//简介
    private String avatarUrl;//头像
    private String avatarUrlLarge;//大ｂ头像
    private String lastPostTime;//最后一次登陆时间
    private String editorNotes;//职业信息

    public HotAuthor(String id, String name, String resume, String persionPagerUrl, String avatarUrl, String avatarUrlLarge, String lastPostTime, String editorNotes, String alt) {
        this.id = id;
        this.name = name;
        this.resume = resume;
        this.persionPagerUrl = persionPagerUrl;
        this.avatarUrl = avatarUrl;
        this.avatarUrlLarge = avatarUrlLarge;
        this.lastPostTime = lastPostTime;
        this.editorNotes = editorNotes;
        this.alt = alt;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getPersionPagerUrl() {
        return persionPagerUrl;
    }

    public void setPersionPagerUrl(String persionPagerUrl) {
        this.persionPagerUrl = persionPagerUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrlLarge() {
        return avatarUrlLarge;
    }

    public void setAvatarUrlLarge(String avatarUrlLarge) {
        this.avatarUrlLarge = avatarUrlLarge;
    }

    public String getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(String lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

    public String getEditorNotes() {
        return editorNotes;
    }

    public void setEditorNotes(String editorNotes) {
        this.editorNotes = editorNotes;
    }
}
