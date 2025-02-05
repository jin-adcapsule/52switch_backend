package com.adacpsule.server52switch.shared.inputs;

import java.util.Objects;

public class GroupInput {
    private final String groupName;
    private final String groupSupervisorOid;
    private final String parentGroupId;

    // Constructor for initialization
    public GroupInput(String groupName, String groupSupervisorOid, String parentGroupId) {
        this.groupName = groupName;
        this.groupSupervisorOid = groupSupervisorOid;
        this.parentGroupId = parentGroupId;
    }

    // Getters
    public String getGroupName() { return groupName; }
    public String getGroupSupervisorOid() { return groupSupervisorOid; }
    public String getParentGroupId() { return parentGroupId; }

    // Overridden toString, equals, and hashCode methods
    @Override
    public String toString() {
        return "GroupInput{" +
                "groupName='" + groupName + '\'' +
                ", groupSupervisorOid='" + groupSupervisorOid + '\'' +
                ", parentGroupId='" + parentGroupId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupInput that = (GroupInput) o;
        return Objects.equals(groupName, that.groupName) &&
               Objects.equals(groupSupervisorOid, that.groupSupervisorOid) &&
               Objects.equals(parentGroupId, that.parentGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, groupSupervisorOid, parentGroupId);
    }
}

