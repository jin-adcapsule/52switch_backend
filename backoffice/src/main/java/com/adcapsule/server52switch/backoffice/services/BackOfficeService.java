package com.adcapsule.server52switch.backoffice.services;

import java.util.ArrayList;
import java.util.List;

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

    public List<Employee> getMyAllGroupsMembers(String employeeOid){
        // Retrieve all groups supervised by the employee
        List<Group> allGroups = groupService.getAllSubGroupsBySupervisorOid(employeeOid);
        System.err.println(allGroups);
        List<Employee> groupMembers = new ArrayList<>();

        for (Group group : allGroups) {
            List<String> groupMemberOids = employeeService.findEmployeeOidListbyGroupId(group.getId());
            for (String memberOid : groupMemberOids) {
                Employee member = employeeService.getEmployeeById(memberOid);
                if (!groupMembers.contains(member)) {
                    groupMembers.add(member);
                }
            }
        }
        System.err.println(groupMembers);
        return groupMembers;
    }
    
}
