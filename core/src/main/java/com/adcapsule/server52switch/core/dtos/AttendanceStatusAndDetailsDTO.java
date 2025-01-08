package com.adcapsule.server52switch.core.dtos;

import java.util.List;

public class AttendanceStatusAndDetailsDTO {
    private Boolean status;
    private List<String> workTypeList;
    private String startTime;
    private String endTime;
    private String locationName;
    // Constructor
    public AttendanceStatusAndDetailsDTO(Boolean status,List<String> workTypeList,String startTime,String endTime, String locationName) {
        this.status = status;
        this.workTypeList = workTypeList;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationName= locationName;
    }

    // Getter
    public Boolean getStatus() {
        return status;
    }
    public List<String> getWorkTypeList() {
        return workTypeList;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getLocationName() {
        return locationName;
    }

}