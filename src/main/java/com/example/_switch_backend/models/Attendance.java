package com.example._switch_backend.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "attendance")
public class Attendance {
    @Id
    private String _id; // This represents the MongoDB `_id` field
    //fed fields from markattendance by pushing button
    private int employeeId;
    private Boolean status; // 1 for present 0 for out of office
    //time is checktime 


    //current date
    private String date; // format 'yyyy-mm-dd'
    // Convert Date to Date
    private Date checkInTime; 
    private Date checkOutTime;
    private String checkInStatus;
    private String checkOutStatus;
    private List<String> workTypeList;
    // Getter and Setter methods


    public String getId() {
        return _id;
    }
    public int  getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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

    
    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }
    public String getCheckOutStatus() {
        return checkOutStatus;
    }

    public void setCheckOutStatus(String checkOutStatus) {
        this.checkOutStatus = checkOutStatus;
    }
    public List<String> getWorkTypeList() {
        return workTypeList;
    }

    public void setWorkTypeList(List<String> workTypeList) {
        this.workTypeList = workTypeList;
    }
}
