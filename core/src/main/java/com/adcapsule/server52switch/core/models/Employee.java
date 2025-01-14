package com.adcapsule.server52switch.core.models;

import java.util.Date;

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
    private Date joindate;
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
    
    public Date getJoindate() {
        // if (joindate == null) {
        //     return null;
        // }
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        // return formatter.format(joindate);
        return joindate;

    }

    public void setJoindate(Date joindate) {
        this.joindate = joindate;
    }

     
    public Integer getDayoffPerYear() {
        return dayoffPerYear;
    }

    public void setDayoffPerYear(Integer dayoffPerYear) {
        this.dayoffPerYear = dayoffPerYear;
    }

}
