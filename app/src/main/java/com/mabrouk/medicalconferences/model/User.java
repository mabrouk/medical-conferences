package com.mabrouk.medicalconferences.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 12/2/2016.
 */

public class User implements Parcelable {
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_DOCTOR = 2;

    final int id;
    final String email;
    final String firstName;
    final String lastName;
    final String password;
    final int role;
    final long lastLoginTimestamp;

    public User(int id, String email, String firstName, String lastName, String password, int role, long lastLoginTimestamp) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.lastLoginTimestamp = lastLoginTimestamp;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }

    public long getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.email);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.password);
        dest.writeInt(this.role);
        dest.writeLong(this.lastLoginTimestamp);
    }

    protected User(Parcel in) {
        this.id = in.readInt();
        this.email = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.password = in.readString();
        this.role = in.readInt();
        this.lastLoginTimestamp = in.readLong();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
