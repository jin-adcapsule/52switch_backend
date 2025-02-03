package com.adcapsule.server52switch.core.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "credential")
public class Credential {
    @Id
    private String _id; // This represents the MongoDB `_id` field

    private String employeeOid;
    private String fcmToken;
    // Getters and Setters

    public String getId() {
        return _id;
    }
    public String getEmployeeOid() {
        return employeeOid;
    }

    public void setEmployeeOid(String employeeOid) {
        this.employeeOid = employeeOid;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
