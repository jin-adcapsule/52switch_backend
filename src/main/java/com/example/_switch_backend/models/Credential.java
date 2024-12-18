package com.example._switch_backend.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "credential")
public class Credential {
    @Id
    private ObjectId _id; // This represents the MongoDB `_id` field

    private String employeeOid;
    private String fcmToken;
    // Getters and Setters
    public ObjectId getIdBson() {
        return _id;
    }
    public String getId() {
        return _id.toString();
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
