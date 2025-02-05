package com.adcapsule.server52switch.core.dtos;



public class EmployeeValDTO {
    private final String employeeOid;
    private final String employeeName;
    private final boolean isSupervisor;
    private final Boolean currentlyMarked;

    // Constructor
    public EmployeeValDTO(
        String employeeOid,
        String employeeName,
        boolean isSupervisor,
        Boolean currentlyMarked) {
            this.employeeOid = employeeOid;
            this.employeeName = employeeName;
            this.isSupervisor = isSupervisor;
            this.currentlyMarked = currentlyMarked;
    }

    // Getter
    public String getEmployeeOid() {
        return employeeOid;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public boolean getIsSupervisor() {
        return isSupervisor;
    }
    public Boolean getCurrentlyMarked() {
        return currentlyMarked;
    }

}