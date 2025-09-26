package com.example.parking.controller;

import com.example.parking.model.VehicleType;

public class EntryRequest {
    private String vehicleNumber;
    private VehicleType vehicleType;
    private Long gateId;

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public Long getGateId() { return gateId; }
    public void setGateId(Long gateId) { this.gateId = gateId; }
}
