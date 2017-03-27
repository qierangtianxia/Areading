package com.qrtx.areading.beans;

/**
 * Created by user on 17-3-20.
 */

public class Comment {
    private String userName;
    private long time;
    private String content;
    private String userhead;

    public Comment(String userName, long time, String content, String userhead) {
        this.userName = userName;
        this.time = time;
        this.content = content;
        this.userhead = userhead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead;
    }

    @Override
    public boolean equals(Object obj) {
        Comment c = (Comment) obj;
        return userName.equals(c.getUserName())
                && content.equals(c.getContent())
                && time == c.getTime();
    }
}
