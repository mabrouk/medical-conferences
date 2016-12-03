package com.mabrouk.medicalconferences.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by User on 12/2/2016.
 */

public class Conference implements Parcelable {

    final int id;
    final String name;
    final int adminId;
    final long dateTimestamp;
    boolean cancelled;
    long updatedAtTimestamp;

    Invitation invitation;

    public Conference(int id, String name, int adminId, long dateTimestamp, boolean cancelled, long updatedAtTimestamp) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
        this.dateTimestamp = dateTimestamp;
        this.cancelled = cancelled;
        this.updatedAtTimestamp = updatedAtTimestamp;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDateTimestamp() {
        return dateTimestamp;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public long getUpdatedAtTimestamp() {
        return updatedAtTimestamp;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setUpdatedAtTimestamp(long updatedAtTimestamp) {
        this.updatedAtTimestamp = updatedAtTimestamp;
    }

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.adminId);
        dest.writeLong(this.dateTimestamp);
        dest.writeByte(this.cancelled ? (byte) 1 : (byte) 0);
        dest.writeLong(this.updatedAtTimestamp);
    }

    protected Conference(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.adminId = in.readInt();
        this.dateTimestamp = in.readLong();
        this.cancelled = in.readByte() != 0;
        this.updatedAtTimestamp = in.readLong();
    }

    public static final Parcelable.Creator<Conference> CREATOR = new Parcelable.Creator<Conference>() {
        @Override
        public Conference createFromParcel(Parcel source) {
            return new Conference(source);
        }

        @Override
        public Conference[] newArray(int size) {
            return new Conference[size];
        }
    };
}
