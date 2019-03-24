package com.mathandoro.coachplus.models;

public class Device {
    private String pushId;
    private String system;
    private String deviceId;

    public Device(String pushId, String system, String deviceId) {
        this.pushId = pushId;
        this.system = system;
        this.deviceId = deviceId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
