package com.alamega.alamegaspringapp.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "info")
public class Info {
    @Id
    @Column(name = "mac", nullable = false)
    private String mac;

    private boolean isOnline;

    @Column(length=1024)
    private String config;

    @Column(length=65536)
    private String data;

    private String currentStatus;

    public Info() {}

    public Info(String mac, String config) {
        this.mac = mac;
        this.isOnline = false;
        this.config = config;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
