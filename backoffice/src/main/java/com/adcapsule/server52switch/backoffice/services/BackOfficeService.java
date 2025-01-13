package com.adcapsule.server52switch.backoffice.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.backoffice.dtos.GroupMembersDTO;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.models.Group; // Calling shared service
import com.adcapsule.server52switch.core.models.Location;
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
        // Retrieve all groups supervised by the employee
        List<Group> allGroups = groupService.getAllSubGroupsBySupervisorOid(employeeOid);
        // Convert the list to a Set to remove duplicates
        Set<Group> uniqueGroups = new HashSet<>(allGroups);
        // Convert the Set back to a List
        List<Group> uniqueGroupsList = new ArrayList<>(uniqueGroups);
        return uniqueGroupsList;
    }
    public List<Location> getAllLocations(){
        return employeeService.findAllLocations();
    }
    public List<GroupMembersDTO> getMyAllGroupsMembers(String employeeOid){
        // Retrieve all groups supervised by the employee
        List<Group> uniqueGroupsList = getMyAllGroups(employeeOid);
        List<GroupMembersDTO> response = new ArrayList<>();
        for (Group group : uniqueGroupsList) {
            System.out.println(group.getGroupName());
            List<Employee> groupMembers = new ArrayList<>();
            
            List<String> groupMemberOids = employeeService.findEmployeeOidListbyGroupId(group.getId());
            for (String memberOid : groupMemberOids) {
                Employee member = employeeService.getEmployeeById(memberOid);
                if (!groupMembers.contains(member)) {
                    groupMembers.add(member);
                }
            }
           // Create a DTO for the group and its members
            GroupMembersDTO groupMembersDTO = new GroupMembersDTO(
                group.getId(), 
                group.getGroupName(), 
                group.getParentGroupId(), 
                group.getGroupSupervisorOid(),
                groupMembers
            );

            response.add(groupMembersDTO); // Add the DTO to the response list
        }

        //System.err.println(groupMembers);
        return response;
    }
    
}
