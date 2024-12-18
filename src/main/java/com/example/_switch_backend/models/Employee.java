package com.example._switch_backend.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee")
public class Employee {
    @Id
    private String _id; // EmployeeOid

    private int employeeId;  
    private String name;
    private String email;
    private String position;
    private String phone;
    private Date joindate;

    private String groupId;
    private String department;
    private int supervisorId= -1; // Default to -1 to represent "null"
    private String supervisorName; 
    private Boolean isSupervisor;

    private String locationId;
    private String workhourOn;
    private String workhourOff;
    private String workhourHalf;
    private String workplace;
    private String workhour;
    private int dayoffremaining=0;

    // Getters and Setters
    // Getters and Setters

    public String getId() {
        return _id;
    }
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getLocationId() {
        return locationId;
    }
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }
    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }
    
    public Boolean getIsSupervisor() {
        return isSupervisor;
    }

    public void setIsSupervisor(Boolean isSupervisor) {
        this.isSupervisor = isSupervisor;
    }
    public String getJoindate() {
        if (joindate == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        return formatter.format(joindate);

    }

    public void setJoindate(Date joindate) {
        this.joindate = joindate;
    }

        /*/ Method to return joindate as a formatted String
    public String getFormattedJoindate() {
        if (joindate == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(joindate);
    }
        */

    public String getWorkhourOn() {
        return workhourOn;
    }

    public void setWorkhourOn(String workhourOn) {
        this.workhourOn = workhourOn;
    }
    public String getWorkhourOff() {
        return workhourOff;
    }

    public void setWorkhourOff(String workhourOff) {
        this.workhourOff = workhourOff;
    }
    public String getWorkhourHalf() {
        return workhourHalf;
    }

    public void setWorkhourHalf(String workhourHalf) {
        this.workhourHalf = workhourHalf;
    }
    public String getWorkhour() {
        return workhourOn+'-'+workhourOff;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public int getDayoffRemaining() {
        return dayoffremaining;
    }

    public void setDayoffRemaining(int dayoffremaining) {
        this.dayoffremaining = dayoffremaining;
    }

}
