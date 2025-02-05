package com.adacpsule.server52switch.shared.inputs;

import java.util.Objects;

public class EmployeeInput {

    private final Integer employeeId;
    private final String name;
    private final String email;
    private final String position;
    private final String phone;
    private final String joindate;
    private final String groupId;
    private final String locationId;
    private final Integer dayoffPerYear;

    // Constructor for initialization
    public EmployeeInput(Integer employeeId, String name, String email, String position, String phone, 
                         String joindate, String groupId, String locationId, Integer dayoffPerYear) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.position = position;
        this.phone = phone;
        this.joindate = joindate;
        this.groupId = groupId;
        this.locationId = locationId;
        this.dayoffPerYear = dayoffPerYear;
    }

    // Getters
    public Integer getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPosition() { return position; }
    public String getPhone() { return phone; }
    public String getJoindate() { return joindate; }
    public String getGroupId() { return groupId; }
    public String getLocationId() { return locationId; }
    public Integer getDayoffPerYear() { return dayoffPerYear; }

    // Overridden toString, equals, and hashCode methods
    @Override
    public String toString() {
        return "EmployeeInput{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", joindate='" + joindate + '\'' +
                ", groupId='" + groupId + '\'' +
                ", locationId='" + locationId + '\'' +
                ", dayoffPerYear=" + dayoffPerYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeInput that = (EmployeeInput) o;
        return Objects.equals(employeeId, that.employeeId) &&
               Objects.equals(name, that.name) &&
               Objects.equals(email, that.email) &&
               Objects.equals(position, that.position) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(joindate, that.joindate) &&
               Objects.equals(groupId, that.groupId) &&
               Objects.equals(locationId, that.locationId) &&
               Objects.equals(dayoffPerYear, that.dayoffPerYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, name, email, position, phone, joindate, groupId, locationId, dayoffPerYear);
    }
}
