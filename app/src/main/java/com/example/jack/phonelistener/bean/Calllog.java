package com.example.jack.phonelistener.bean;

/**
 * Created by jack on 17-7-3.
 */

public class Calllog {

    private long id;
    private String phone;
    private long createTime;
    private long duration;
    private String file;

    public Calllog(String phone, long createTime, long duration, String file) {
        this.phone = phone;
        this.createTime = createTime;
        this.duration = duration;
        this.file = file;
    }

    public Calllog() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
