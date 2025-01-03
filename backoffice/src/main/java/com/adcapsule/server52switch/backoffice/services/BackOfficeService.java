package com.adcapsule.server52switch.backoffice.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.core.models.Employee; // Calling shared service
import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.services.EmployeeService;
import com.adcapsule.server52switch.core.services.GroupService;

@Service
public class BackOfficeService {

    private final EmployeeService employeeService;
    private final GroupService groupService;

    @Autowired
    public BackOfficeService(EmployeeService employeeService,GroupService groupService) {
        this.employeeService = employeeService;
        this.groupService = groupService;
    }
    public List<Group> getMyAllGroups(String employeeOid) {
        List<Group> allGroups=groupService.getAllSubGroupsBySupervisorOid(employeeOid); // Calling shared service method
        return allGroups;
    }

    public Map<String,List<Employee>> getMyAllGroupsMembers(String employeeOid){
        // Retrieve all groups supervised by the employee
        List<Group> allGroups=groupService.getAllSubGroupsBySupervisorOid(employeeOid); // Calling shared service method
        // Map to store supervisorOid as key and member OIDs as value
        Map<String,List<Employee>> memberMapBySupervisorOid = new HashMap<>();
        for (Group group : allGroups){
            String supervisorOid = group.getGroupSupervisorOid();
            // Fetch the list of member OIDs for the current group
            List<String> groupMemberOids = employeeService.findEmployeeOidListbyGroupId(group.getId());
            // Add the groupMemberOids to the corresponding supervisorOid in the map
            memberMapBySupervisorOid.computeIfAbsent(supervisorOid, k -> new ArrayList<>());
            // Avoid adding duplicate OIDs
            for (String memberOid : groupMemberOids) {
                Employee member = employeeService.getEmployeeById(memberOid);
                if (!memberMapBySupervisorOid.get(supervisorOid).contains(member)) {
                    memberMapBySupervisorOid.get(supervisorOid).add(member);
                }
            }
        }

        return memberMapBySupervisorOid;
    }
    
}
