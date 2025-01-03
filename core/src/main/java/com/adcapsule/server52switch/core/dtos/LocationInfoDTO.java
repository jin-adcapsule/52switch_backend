package com.adcapsule.server52switch.core.dtos;



public class LocationInfoDTO {
    private String workhourOn;
    private String workhourOff;
    private String workhourHalf;
    private String workplace;

    // Constructor
    public LocationInfoDTO(
        String workplace,
        String workhourOn,
        String workhourOff,
        String workhourHalf) {
            this.workplace = workplace;
            this.workhourOn = workhourOn;
            this.workhourOff = workhourOff;
            this.workhourHalf = workhourHalf;
    }

    // Getter
    public String getWorkplace() {
        return workplace;
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

}