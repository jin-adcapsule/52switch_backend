package com.adcapsule.server52switch.core.dtos;



public class DayoffInfoDTO {
    private final String supervisorName;
    private final String supervisorOid;
    private final Integer dayoffPerYear;

    // Constructor
    public DayoffInfoDTO(
        String supervisorName,
        String supervisorOid,
        Integer dayoffPerYear) {
            this.supervisorName = supervisorName;
            this.supervisorOid = supervisorOid;
            this.dayoffPerYear = dayoffPerYear;
    }

    // Getter
    public String getSupervisorName() {
        return supervisorName;
    }

    public String getSupervisorOid() {
        return supervisorOid;
    }

    public Integer getDayoffPerYear() {
        return dayoffPerYear;
    }


}