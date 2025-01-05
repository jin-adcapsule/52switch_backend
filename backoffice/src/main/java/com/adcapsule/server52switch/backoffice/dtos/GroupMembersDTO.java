package com.adcapsule.server52switch.backoffice.dtos;

import java.util.List;

import com.adcapsule.server52switch.core.models.Employee;

public class GroupMembersDTO {
    private String groupName;
    private List<Employee> members;

    public GroupMembersDTO(String groupName, List<Employee> members) {
        this.groupName = groupName;
        this.members = members;
    }

    // Getters and Setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Employee> getMembers() {
        return members;
    }

    public void setMembers(List<Employee> members) {
        this.members = members;
    }
}
