package com.dt5000.ischool.activity.media.bean;

/**
 * @author lijian
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.bilibili.boxing.model.entity.BaseMedia;

public class MMImageBean extends BaseMedia implements Parcelable {

    private String path = null;
    private boolean isSelected = false;
    private boolean selectable = true;
    private String thumbnails = null;

    @Override
    public TYPE getType() {
        return null;
    }

    public MMImageBean(String path, boolean selected, String thumbnails) {
        this.path = path;
        this.isSelected = selected;
        this.thumbnails = thumbnails;
    }

    public MMImageBean(String path) {
        this.path = path;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSeleted the isSelected to set
     */
    public void setSelected(boolean isSeleted) {
        this.isSelected = isSeleted;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(selectable ? (byte) 1 : (byte) 0);
        dest.writeString(this.thumbnails);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof MMImageBean) {
            MMImageBean imageBean = (MMImageBean) obj;
            if (path == null) {
                if (imageBean.getPath() == null)
                    return true;
            } else if (path.equals(imageBean.getPath())) {
                return true;
            }
        }
        return false;
    }

    protected MMImageBean(Parcel in) {
        this.path = in.readString();
        this.isSelected = in.readByte() != 0;
        this.selectable = in.readByte() != 0;
        this.thumbnails = in.readString();
    }

    public static final Creator<MMImageBean> CREATOR = new Creator<MMImageBean>() {
        public MMImageBean createFromParcel(Parcel source) {
            return new MMImageBean(source);
        }

        public MMImageBean[] newArray(int size) {
            return new MMImageBean[size];
        }
    };

    @Override
    public String toString() {
        return "MMImageBean{" +
                "isSelected=" + isSelected +
                ", path='" + path + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                '}';
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
}
