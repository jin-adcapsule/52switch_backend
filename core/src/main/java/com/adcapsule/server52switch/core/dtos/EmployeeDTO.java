package com.adcapsule.server52switch.core.dtos;

public class EmployeeDTO {
    private String employeeOid; // EmployeeOid
    private Integer employeeId;  
    private String name;
    private String email;
    private String position;
    private String phone;
    private String joindate;
    private String groupId;
    private String locationId;
    private Integer dayoffPerYear;


    private String department;
    private String supervisorOid;
    private String supervisorName; 
    private Boolean isSupervisor;


    private String workhourOn;
    private String workhourOff;
    private String workhourHalf;
    private String workplace;

    // Constructor
    public EmployeeDTO(
        String employeeOid, // EmployeeOid
        Integer employeeId,
        String name,
        String email,
        String position,
        String phone,
        String joindate,
        String groupId,
        String locationId,
        Integer dayoffPerYear,

        String department,
        String supervisorOid,
        String supervisorName, 
        Boolean isSupervisor,

        String workhourOn,
        String workhourOff,
        String workhourHalf,
        String workplace
        ) {
            this.employeeOid = employeeOid;
            this.employeeId = employeeId;
            this.name = name;
            this.email = email;
            this.position = position;
            this.phone = phone;
            this.joindate = joindate;
            this.groupId = groupId;
            this.locationId = locationId;
            this.dayoffPerYear = dayoffPerYear;

            this.department = department;
            this.supervisorOid = supervisorOid;
            this.supervisorName = supervisorName;
            this.isSupervisor = isSupervisor;

            this.workhourOn = workhourOn;
            this.workhourOff = workhourOff;
            this.workhourHalf = workhourHalf;
            this.workplace = workplace;

    }
    // Getters and Setters
    // Getters and Setters

    public String getEmployeeOId() {
        return employeeOid;
    }
    public Integer getEmployeeId() {
        return employeeId;
    }


    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public String getDepartment() {
        return department;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getPosition() {
        return position;
    }


    public String getPhone() {
        return phone;
    }

    public String getSupervisorOid() {
        return supervisorOid;
    }

        
    public String getSupervisorName() {
        return supervisorName;
    }

    
    public Boolean getIsSupervisor() {
        return isSupervisor;
    }


    public String getJoindate() {

        return joindate;

    }

    public String getWorkhourOn() {
        return workhourOn;
    }


    public String getWorkhourOff() {
        return workhourOff;
    }


    public String getWorkhourHalf() {
        return workhourHalf;
    }


    public String getWorkhour() {
        return workhourOn+'-'+workhourOff;
    }

    public String getWorkplace() {
        return workplace;
    }


    public Integer getDayoffPerYear() {
        return dayoffPerYear;
    }





}
