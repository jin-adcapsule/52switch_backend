package com.adcapsule.server52switch.core.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "attendance")
public class Attendance {
    @Id
    private String _id; // This represents the MongoDB `_id` field
    private String employeeOid;
    private Boolean status;
    //time is checktime 


    //current date
    private String date; // format 'yyyy-mm-dd'
    private Date checkInTime; 
    private Date checkOutTime;
    private String locationId;

    // Getter and Setter methods


    public String getId() {
        return _id;
    }
    public String  getEmployeeOid() {
        return employeeOid;
    }

    public void setEmployeeOid(String employeeOid) {
        this.employeeOid = employeeOid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCheckInTime() {
        
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        
        this.checkInTime = checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    public String getLocationId() {
        return locationId;
    }
    
}
