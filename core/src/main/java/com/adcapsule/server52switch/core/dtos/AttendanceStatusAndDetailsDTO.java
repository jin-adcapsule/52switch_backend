package com.adcapsule.server52switch.core.dtos;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.adcapsule.server52switch.core.configs.Config;

public class AttendanceStatusAndDetailsDTO {
    private Boolean status;
    private List<String> workTypeList;
    private String startTime;
    private String endTime;
    private String locationName;
    // Constructor
    public AttendanceStatusAndDetailsDTO(Boolean status,List<String> workTypeList,String startTime,String endTime, String locationName) {
        this.status = (startTime==""&&endTime=="")?null:status; //if today dayoff then status null
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
        // Map workTypeList items from values to text
        return workTypeList.stream()
            .map(key -> Config.workTypeToTextMap.get(key)) // Get mapped value
            .filter(Objects::nonNull)    // Exclude null values in case of unmatched keys
            .map(String.class::cast)                      // Ensure the result is String
            .collect(Collectors.toList());
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