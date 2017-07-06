package com.example.jack.phonelistener.bean;

import android.util.Log;

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

    @Override
    public boolean equals(Object obj) {
//        return super.equals(obj);
        if (obj == null) return false;
        if (!(obj instanceof Calllog)) return false;
        Calllog calllog = (Calllog) obj;
        Log.d("phonelistener_tag", "-- equals --");
        Log.d("phonelistener_tag", "this.id == calllog.id: " + (this.id == calllog.id));
        Log.d("phonelistener_tag", "this.phone.equals(calllog.phone): " + (this.phone.equals(calllog.phone)));
        Log.d("phonelistener_tag", "this.createTime == calllog.createTime: " + (this.createTime == calllog.createTime));
        Log.d("phonelistener_tag", "this.duration == calllog.duration: " + (this.duration == calllog.duration));
        Log.d("phonelistener_tag", "this.file.equals(calllog.file): " + (this.file.equals(calllog.file)));
        Log.d("phonelistener_tag", "-- end equals --");
        if (this.id == calllog.id
                && this.phone.equals(calllog.phone)
                && this.createTime == calllog.createTime
                && this.duration == calllog.duration
                && this.file.equals(calllog.file)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Calllog{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", createTime=" + createTime +
                ", duration=" + duration +
                ", file='" + file + '\'' +
                '}';
    }
}
