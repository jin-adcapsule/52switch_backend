package com.adcapsule.server52switch.core.dtos;



public class DayoffInfoDTO {
    private String supervisorName;
    private String supervisorOid;
    private int dayoffRemaining;

    // Constructor
    public DayoffInfoDTO(
        String supervisorName,
        String supervisorOid,
        int dayoffRemaining) {
            this.supervisorName = supervisorName;
            this.supervisorOid = supervisorOid;
            this.dayoffRemaining = dayoffRemaining;
    }

    // Getter
    public String getSupervisorName() {
        return supervisorName;
    }

    public String getSupervisorOid() {
        return supervisorOid;
    }

    public int getDayoffRemaining() {
        return dayoffRemaining;
    }


}