package com.mabrouk.medicalconferences.model;

/**
 * Created by User on 12/2/2016.
 */

public class Conference {

    final int id;
    final String name;
    final long dateTimestamp;
    boolean cancelled;
    long updatedAtTimestamp;

    public Conference(int id, String name, long dateTimestamp, boolean cancelled, long updatedAtTimestamp) {
        this.id = id;
        this.name = name;
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

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setUpdatedAtTimestamp(long updatedAtTimestamp) {
        this.updatedAtTimestamp = updatedAtTimestamp;
    }
}
