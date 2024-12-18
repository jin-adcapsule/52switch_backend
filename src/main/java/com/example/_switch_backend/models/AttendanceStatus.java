package com.example._switch_backend.models;

import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "attendance")
public class AttendanceStatus {
    private String employeeOid;
    private String date;
    private boolean status;

    // Constructor, Getters, and Setters
    public AttendanceStatus(String employeeOId, String date, boolean status) {
        this.employeeOid = employeeOId;
        this.date = date;
        this.status = status;
    }
    
    public String getEmployeeOid() {
        return employeeOid;
    }

    public String getDate() {
        return date;
    }

    public boolean isStatus() {
        return status;
    }
    @Override
    public String toString() {
        return "AttendanceStatus{" +
               "employeeOid='" + employeeOid + '\'' +
               ", date='" + date + '\'' +
               ", status=" + status +
               '}';
    }
}