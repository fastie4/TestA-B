package com.fastie4.testa.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private long id;
    private String link;
    private int status;
    private long time;

    public Item() {}

    protected Item(Parcel in) {
        id = in.readLong();
        link = in.readString();
        status = in.readInt();
        time = in.readLong();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(link);
        parcel.writeInt(status);
        parcel.writeLong(time);
    }
}
