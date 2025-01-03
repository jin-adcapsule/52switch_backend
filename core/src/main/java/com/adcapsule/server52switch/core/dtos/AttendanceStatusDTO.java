package com.adcapsule.server52switch.core.dtos;


public class AttendanceStatusDTO {
    private Boolean status;

    // Constructor
    public AttendanceStatusDTO(Boolean status) {
        this.status = status;
    }

    // Getter
    public Boolean getStatus() {
        return status;
    }

}