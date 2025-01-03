package com.adcapsule.server52switch.core.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "dayoff")
public class Dayoff {
    @Id
    private String _id;

    private String employeeOid;
    private int employeeId;
    private String dayoffType;
    private String requestComment;
    private String dayoffDate;
    private String requestStatus;
    private String requestDate;
    private String requestKey;
    private String answerComment;
    private int supervisorId;
    private int beforeDateRemaining;
    // Getters and Setters
    public String getEmployeeOId() {
        return employeeOid;
    }
    public void setEmployeeOId(String employeeOid) {
        this.employeeOid = employeeOid;
    }
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getDayoffType() {
        return dayoffType;
    }

    public void setDayoffType(String dayoffType) {
        this.dayoffType = dayoffType;
    }
    public String getRequestComment() {
        return requestComment;
    }

    public void setRequestComment(String requestComment) {
        this.requestComment = requestComment;
    }
    public String getDayoffDate() {
        return dayoffDate;
    }

    public void setDayoffDate(String dayoffDate) {
        this.dayoffDate = dayoffDate;
    }
    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }
    public String getAnswerComment() {
        return answerComment;
    }

    public void setAnswerComment(String answerComment) {
        this.answerComment = answerComment;
    }
    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }
    public int getSupervisorId() {
        return supervisorId;
    }
    public void setBeforeDateRemaining(int beforeDateRemaining) {
        this.beforeDateRemaining = beforeDateRemaining;
    }
    public int getBeforeDateRemaining() {
        return beforeDateRemaining;
    }
}
