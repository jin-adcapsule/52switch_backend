package com.adcapsule.server52switch.backoffice.dtos;

import java.util.List;

import com.adcapsule.server52switch.core.dtos.EmployeeDTO;

public class GroupMembersDTO {
    private String groupId;
    private String groupName;
    private String parentGroupId;
    private String groupSupervisorOid;
    private List<EmployeeDTO> members;

    public GroupMembersDTO(String groupId, String groupName, String parentGroupId, String groupSupervisorOid,List<EmployeeDTO> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.parentGroupId = parentGroupId;
        this.groupSupervisorOid = groupSupervisorOid;
        this.members = members;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
    public String getGroupSupervisorOid() {
        return groupSupervisorOid;
    }
    public void setGroupSupervisorOid(String groupSupervisorOid) {
        this.groupSupervisorOid = groupSupervisorOid;
    }

    public List<EmployeeDTO> getMembers() {
        return members;
    }

    public void setMembers(List<EmployeeDTO> members) {
        this.members = members;
    }

}
