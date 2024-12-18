package com.example._switch_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "location")
public class Location {
     @Id
    private String _id; // This represents the MongoDB `_id` field

    private String workplace;
    private String workhourOn;
    private String workhourOff;
    private String workhourHalf;

    public String getId() {
        return _id;
    }
    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }


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
}
