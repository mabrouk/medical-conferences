package com.mabrouk.medicalconferences.model;

/**
 * Created by User on 12/2/2016.
 */

public class User {
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
}
