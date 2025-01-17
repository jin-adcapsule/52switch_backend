package com.adacpsule.server52switch.shared.inputs;
import java.util.Objects;

public class DayoffInput {
    private  String employeeOid;
    private final String dayoffType;
    private final String requestComment;
    private  String dayoffDate;
    private final String requestStatus;
    private final String requestDate;
    private final String supervisorOid;
    private final String requestKey;

    // Constructor for initialization
    public DayoffInput(String employeeOid, String dayoffType, String requestComment, String dayoffDate,
                       String requestStatus, String requestDate, String supervisorOid, String requestKey) {
        this.employeeOid = employeeOid;
        this.dayoffType = dayoffType;
        this.requestComment = requestComment;
        this.dayoffDate = dayoffDate;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
        this.supervisorOid = supervisorOid;
        this.requestKey = requestKey;
    }

    // Getters
    public String getEmployeeOid() { return employeeOid; }
    public String getDayoffType() { return dayoffType; }
    public String getRequestComment() { return requestComment; }
    public String getDayoffDate() { return dayoffDate; }
    public String getRequestStatus() { return requestStatus; }
    public String getRequestDate() { return requestDate; }
    public String getSupervisorOid() { return supervisorOid; }
    public String getRequestKey() { return requestKey; }

    //Setters
    public void setDayoffDate(String dayoffDate) {  this.dayoffDate=dayoffDate; }
    public void setEmployeeOid(String employeeOid) {  this.employeeOid=employeeOid; }


    // Overridden toString, equals, and hashCode methods
    @Override
    public String toString() {
        return "DayoffInput{" +
                "employeeOid='" + employeeOid + '\'' +
                ", dayoffType='" + dayoffType + '\'' +
                ", requestComment='" + requestComment + '\'' +
                ", dayoffDate='" + dayoffDate + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", requestDate='" + requestDate + '\'' +
                ", supervisorOid='" + supervisorOid + '\'' +
                ", requestKey='" + requestKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayoffInput that = (DayoffInput) o;
        return Objects.equals(employeeOid, that.employeeOid) &&
               Objects.equals(dayoffType, that.dayoffType) &&
               Objects.equals(requestComment, that.requestComment) &&
               Objects.equals(dayoffDate, that.dayoffDate) &&
               Objects.equals(requestStatus, that.requestStatus) &&
               Objects.equals(requestDate, that.requestDate) &&
               Objects.equals(supervisorOid, that.supervisorOid) &&
               Objects.equals(requestKey, that.requestKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeOid, dayoffType, requestComment, dayoffDate, requestStatus, requestDate, supervisorOid, requestKey);
    }
}
