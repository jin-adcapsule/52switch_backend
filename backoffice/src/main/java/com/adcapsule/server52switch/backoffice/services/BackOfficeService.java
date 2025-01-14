package com.adcapsule.server52switch.backoffice.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adcapsule.server52switch.backoffice.dtos.GroupMembersDTO;
import com.adcapsule.server52switch.core.dtos.EmployeeDTO;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.models.Location; // Calling shared service
import com.adcapsule.server52switch.core.services.AttendanceService;
import com.adcapsule.server52switch.core.services.DayoffService;
import com.adcapsule.server52switch.core.services.EmployeeService;
import com.adcapsule.server52switch.core.services.GroupService;
import com.adcapsule.server52switch.core.services.LocationService;

@Service
public class BackOfficeService {

    private final EmployeeService employeeService;
    private final GroupService groupService;
    private final LocationService locationService;
    private final DayoffService dayoffService;
    private final AttendanceService attendanceService;

    @Autowired
    public BackOfficeService(EmployeeService employeeService,GroupService groupService,LocationService locationService,DayoffService dayoffService,AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.groupService = groupService;
        this.locationService = locationService;
        this.dayoffService = dayoffService;
        this.attendanceService = attendanceService;
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
    public List<Group> getAllGroups() {
        return groupService.findAll();
    }
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }
    public List<Location> getAllLocations(){
        return locationService.findAll();
    }
    public List<Dayoff> getAllDayoffs() {
        return dayoffService.findAll();
    }
    public List<Attendance> getAllAttendances() {
        return attendanceService.findAll();
    }
    public List<GroupMembersDTO> getMyAllGroupsMembers(String employeeOid){
        // Retrieve all groups supervised by the employee
        List<Group> uniqueGroupsList = getMyAllGroups(employeeOid);
        List<GroupMembersDTO> response = new ArrayList<>();
        for (Group group : uniqueGroupsList) {
            System.out.println(group.getGroupName());
            List<EmployeeDTO> groupMembers = new ArrayList<>();
            
            List<String> groupMemberOids = employeeService.findEmployeeOidListbyGroupId(group.getId());
            for (String memberOid : groupMemberOids) {
                EmployeeDTO member = employeeService.getEmployeeDTOById(memberOid);
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
