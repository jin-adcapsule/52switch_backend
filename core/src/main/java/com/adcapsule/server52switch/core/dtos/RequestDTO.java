package com.adcapsule.server52switch.core.dtos;

import java.util.List;

import com.adcapsule.server52switch.core.configs.DateUtils;

public class RequestDTO {
    //private int employeeId;
    private final String employeeOid;
    private final String employeeName;
    private final String requestType;
    private final String requestStatus;
    //private int supervisorId;
    private final String supervisorOid;
    private final String requestKey;
    private final String requestDate;
    private final String requestComment;

// Optional fields for specific collections
    private String dayoffType;
    private List<String> dayoffDates;
    private String dayoffDateText;



    public RequestDTO(
        String employeeOid,
        String employeeName,
        String requestType,
        String requestStatus,
        //int supervisorId,
        String supervisorOid,
        String requestKey,
        String requestDate,
        String requestComment
        ) {
        this.employeeOid = employeeOid;
        this.employeeName = employeeName;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        //this.supervisorId = supervisorId;
        this.supervisorOid = supervisorOid;
        this.requestKey = requestKey;
        this.requestDate = requestDate;
        this.requestComment = requestComment;

    }
    // Dynamic method to set dayoff optional fields
    public void setDayoffOptionalFields(String dayoffType, List<String> dayoffDates) {
        this.dayoffType = dayoffType;
        this.dayoffDates = dayoffDates;
        this.dayoffDateText = DateUtils.getDateListToText(dayoffDates);
    }
    // Getters and Setters
    /* 
    public int getEmployeeId() {
        return employeeId;
    }
        */
    public String getEmployeeOid() {
        return employeeOid;
    }
    public String getEmployeeName() {
        return employeeName;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestStatus() {
        return requestStatus;
    }
    /* 
    public int getSupervisorId() {
        return supervisorId;
    }
    */
    public String getSupervisorOid() {
        return supervisorOid;
    }
    public String getRequestKey() {
        return requestKey;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getRequestComment() {
        return requestComment;
    }

    public String getDayoffType() {
        return dayoffType;
    }

    public List<String> getDayoffDates() {
        return dayoffDates;
    }
    public String getDayoffDateText() {
        return dayoffDateText;
    }
}