package com.adcapsule.server52switch.core.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "employee")
public class Employee {
    @Id
    private String _id; // EmployeeOid
    private Integer employeeId;  
    private String name;
    private String email;
    private String position;
    private String phone;
    private String joindate;//yyyy-mm-dd format String
    private String groupId;
    private String locationId;
    private Integer dayoffPerYear;


    public String getId() {
        return _id;
    }
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
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
    
    public String getJoindate() {
        return joindate;

    }

    public void setJoindate(String joindate) {

        this.joindate = joindate;
    }

     
    public Integer getDayoffPerYear() {
        return dayoffPerYear;
    }

    public void setDayoffPerYear(Integer dayoffPerYear) {
        this.dayoffPerYear = dayoffPerYear;
    }
    @Override
    public String toString() {
        return "Employee{" +
                "_id='" + _id + '\'' +
                ", employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", joindate='" + joindate + '\'' +
                ", groupId='" + groupId + '\'' +
                ", locationId='" + locationId + '\'' +
                ", dayoffPerYear=" + dayoffPerYear +
                '}';
    }
}
