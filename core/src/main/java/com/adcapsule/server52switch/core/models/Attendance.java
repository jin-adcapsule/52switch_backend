package com.adcapsule.server52switch.core.models;

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
    private String date; //yyyy-mm-dd format String considering custom daystarting hour
    private Long checkInTime; 
    private Long checkOutTime;
    private String locationId;//locationId registered by bluetooth device 

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
        this.date= date;
        // if(DateUtils.isValidDateString(date)){
        //     this.date = date;
        // }else{
        //     this.date = null;
        // }
 
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    public String getLocationId() {
        return locationId;
    }
    
}
