package com.uniview.imos.bigdata.entity;


public class TrafficBean {
    
    private String trafficRowKey;
    private String DeviceID;
    private String Direction;
    private String DrivingStatusCode;
    

    public String getTrafficRowKey() {
        return trafficRowKey;
    }
    public void setTrafficRowKey(String trafficRowKey) {
        this.trafficRowKey = trafficRowKey;
    }
    public String getDeviceID() {
        return DeviceID;
    }
    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }
    public String getDirection() {
        return Direction;
    }
    public void setDirection(String direction) {
        Direction = direction;
    }
    public String getDrivingStatusCode() {
        return DrivingStatusCode;
    }
    public void setDrivingStatusCode(String drivingStatusCode) {
        DrivingStatusCode = drivingStatusCode;
    }
    
    
}
