package com.example._switch_backend.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group")
public class Group {
    @Id
    private String _id; // This represents the MongoDB `_id` field

    private String groupName;
    private String groupSupervisorRole;
    private int groupSupervisorEid;
    private List<String> subgroup;
    private String parentGroup; 

    // Getters and Setters

    public String getId() {
        return _id;
    }
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupSupervisorRole() {
        return groupSupervisorRole;
    }

    public void setGroupSupervisorRole(String groupSupervisorRole) {
        this.groupSupervisorRole = groupSupervisorRole;
    }

    public int getGroupSupervisorEid() {
        return groupSupervisorEid;
    }

    public void setGroupSupervisorEid(int groupSupervisorEid) {
        this.groupSupervisorEid = groupSupervisorEid;
    }


    public List<String> getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(List<String> subgroup) {
        this.subgroup = subgroup;
    }

    public String getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(String parentGroup) {
        this.parentGroup = parentGroup;
    }
}
