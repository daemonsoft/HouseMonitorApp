package me.daemonsoft.housemonitor.models;

/**
 * Created by daemonsoft on 6/06/18.
 */

public class Device {

    private String id;
    private String name;
    private int status;

    public Device() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
