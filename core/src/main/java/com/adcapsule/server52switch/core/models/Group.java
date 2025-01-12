package com.adcapsule.server52switch.core.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group")
public class Group {
    @Id
    private String _id; // This represents the MongoDB `_id#` field

    private String groupName;
    private String groupSupervisorOid;
    private List<String> subGroup;
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



    public String getGroupSupervisorOid() {
        return groupSupervisorOid;
    }

    public void setGroupSupervisorEid(String groupSupervisorOid) {
        this.groupSupervisorOid = groupSupervisorOid;
    }


    public List<String> getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(List<String> subGroup) {
        this.subGroup = subGroup;
    }

    public String getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(String parentGroup) {
        this.parentGroup = parentGroup;
    }
}
