package com.adcapsule.server52switch.core.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group")
public class Group {
    @Id
    private String _id; // This represents the MongoDB `_id#` field

    private String groupName;
    private String groupSupervisorOid;
    private String parentGroupId; 

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



    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroup(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
}
