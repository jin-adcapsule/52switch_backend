package com.adacpsule.server52switch.shared.inputs;

import java.util.Objects;

public class AttendanceInput {
    
    private String date;           // Date as String (e.g., "yyyy-mm-dd")
    private Long checkInTime;      // Nullable Long for check-in time
    private Long checkOutTime;     // Nullable Long for check-out time
    private String locationId;     // Location ID as String
    private Boolean status;        // Attendance status (true/false)
    private String employeeOid;    // Employee OID as String
    
    // Constructors
    public AttendanceInput() {
    }

    public AttendanceInput(String date, Long checkInTime, Long checkOutTime, 
                           String locationId, Boolean status, String employeeOid) {
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.locationId = locationId;
        this.status = status;
        this.employeeOid = employeeOid;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Long checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Long getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Long checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getEmployeeOid() {
        return employeeOid;
    }

    public void setEmployeeOid(String employeeOid) {
        this.employeeOid = employeeOid;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// Overriding function for better human readability and logical compare rather than print class name or reference compare 
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return "AttendanceInput{" +
                "date='" + date + '\'' +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", locationId='" + locationId + '\'' +
                ", status=" + status +
                ", employeeOid='" + employeeOid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceInput that = (AttendanceInput) o;
        return Objects.equals(date, that.date) &&
               Objects.equals(checkInTime, that.checkInTime) &&
               Objects.equals(checkOutTime, that.checkOutTime) &&
               Objects.equals(locationId, that.locationId) &&
               Objects.equals(status, that.status) &&
               Objects.equals(employeeOid, that.employeeOid);
    }
    @Override
    public int hashCode() {
        return Objects.hash(date, checkInTime, checkOutTime, locationId, status, employeeOid);
    }
}