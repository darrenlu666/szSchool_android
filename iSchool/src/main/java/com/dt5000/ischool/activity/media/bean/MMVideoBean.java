package com.dt5000.ischool.activity.media.bean;

/**
 * @author eachann
 */

import android.os.Parcel;
import android.os.Parcelable;

public class MMVideoBean implements Parcelable {
    private String name = null;
    private String path = null;
    private String time = null;
    private String thumb = null;

    public MMVideoBean(String name, String path, String time, String thumb) {
        this.name = name;
        this.path = path;
        this.time = time;
        this.thumb = thumb;
    }

    public MMVideoBean(String name, String path, String time) {
        this.name = name;
        this.path = path;
        this.time = time;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   
    @Override
    public String toString() {
        return "MMVideoBean{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.time);
        dest.writeString(this.thumb);
    }

    protected MMVideoBean(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.time = in.readString();
        this.thumb = in.readString();
    }

    public static final Creator<MMVideoBean> CREATOR = new Creator<MMVideoBean>() {
        @Override
        public MMVideoBean createFromParcel(Parcel source) {
            return new MMVideoBean(source);
        }

        @Override
        public MMVideoBean[] newArray(int size) {
            return new MMVideoBean[size];
        }
    };
}
