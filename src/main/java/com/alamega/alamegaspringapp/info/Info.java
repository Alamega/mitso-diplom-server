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

    private String dynamicData;

    private String staticData;

    private boolean isOnline;

    private String lastUpdateTime;

    public Info() {
        this.dynamicData = "";
        this.staticData = "";
        this.isOnline = false;
        this.lastUpdateTime = "";
    }

    public Info(String mac, String dynamicData) {
        this.mac = mac;
        this.dynamicData = dynamicData;
        this.staticData = "";
        this.isOnline = false;
        this.lastUpdateTime = "";
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(String dynamicData) {
        this.dynamicData = dynamicData;
    }

    public String getStaticData() {
        return staticData;
    }

    public void setStaticData(String staticData) {
        this.staticData = staticData;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
