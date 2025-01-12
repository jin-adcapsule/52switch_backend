package com.adcapsule.server52switch.core.dtos;

import java.util.List;

import com.adcapsule.server52switch.core.models.Holiday;

public class DayoffInfoDTO {
    private final String supervisorName;
    private final String supervisorOid;
    private final Integer dayoffRemaining;
    private List<Holiday> holidayList; // Add Holiday list directly

    // Constructor
    public DayoffInfoDTO(
        String supervisorName,
        String supervisorOid,
        Integer dayoffRemaining,
        List<Holiday> holidayList) {
            this.supervisorName = supervisorName;
            this.supervisorOid = supervisorOid;
            this.dayoffRemaining = dayoffRemaining;
            this.holidayList = holidayList;
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

    public List<Holiday> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<Holiday> holidayList) {
        this.holidayList = holidayList;
    }
}