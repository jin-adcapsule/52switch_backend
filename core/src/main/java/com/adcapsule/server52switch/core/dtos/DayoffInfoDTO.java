package com.adcapsule.server52switch.core.dtos;



public class DayoffInfoDTO {
    private final String supervisorName;
    private final String supervisorOid;
    private final Integer dayoffRemaining;

    // Constructor
    public DayoffInfoDTO(
        String supervisorName,
        String supervisorOid,
        Integer dayoffRemaining) {
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

    public Integer getDayoffRemaining() {
        return dayoffRemaining;
    }


}