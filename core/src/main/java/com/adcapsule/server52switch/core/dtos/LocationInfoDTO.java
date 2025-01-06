package com.adcapsule.server52switch.core.dtos;



public class LocationInfoDTO {
    private final String workhourOn;
    private final String workhourOff;
    private final String workhourHalf;
    private final String workplace;

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
    public String getTimebyKey(String key){
        if ("workhourOn".equals(key)) {
            return workhourOn;
        }
        if ("workhourHalf".equals(key)) {
            return workhourHalf;
        }
        if ("workhourOff".equals(key)) {
            return workhourOff;
        }
        return null;  //
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