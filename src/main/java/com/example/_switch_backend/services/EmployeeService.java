package com.example._switch_backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example._switch_backend.models.Employee;
import com.example._switch_backend.models.Group;
import com.example._switch_backend.models.Location;
import com.example._switch_backend.repositories.EmployeeRepository;
import com.example._switch_backend.repositories.projection.Projection.EmployeeIdProjection;
import com.example._switch_backend.repositories.projection.Projection.GroupIdProjection;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    
    private final GroupService groupService; 
    private final LocationService locationService; 

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, GroupService groupService, LocationService locationService) {
        this.employeeRepository = employeeRepository;

        this.groupService = groupService;
        this.locationService = locationService;
        
    }


    public List<Integer> findEmployeeIdListbyGroupId(String groupId){
        
        // Map the projections to a list of strings (dayoffType)
        return employeeRepository.findEmployeeIdListbyGroupId(groupId).stream()
                      .map(EmployeeIdProjection::getEmployeeId)
                      .collect(Collectors.toList());
    }
    public String findGroupIdbyEmployeeId(int employeeId){
        GroupIdProjection groupIdProjection = employeeRepository.findGroupIdByEmployeeId(employeeId);
        return groupIdProjection != null ? groupIdProjection.getGroupId() : null; // Return groupId or null if not found
    }
/**
     * Retrieve an employee by their unique ObjectId.
     *
     * This method fetches an employee document from the repository based on its ObjectId.
     * Additionally, it resolves the group and location information for the employee.
     *
     * Edge cases:
     * - Throws an exception if the ObjectId format is invalid or the employee is not found.
     *
     * @param objectId The string representation of the ObjectId.
     * @return The Employee object with all resolved fields.
     */
    public Employee getEmployeeById(String objectId) {
        // Convert String to ObjectId
        ObjectId _id;
        try {
            _id = new ObjectId(objectId); // Convert String to ObjectId
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ObjectId format: " + objectId);
        }
        Employee employee = employeeRepository.findById(objectId)
            .orElseThrow(() -> new RuntimeException("Employee not found with Id"));
        
        resolveGroupInfo(employee);
        resolveLocationInfo(employee);
        return employee;
    }
     /**
     * Resolve and set group-related information for an employee.
     *
     * This method determines the group and supervisor details for the given employee.
     *
     * Edge cases:
     * - Throws an exception if the group or supervisor cannot be found.
     * - Handles cases where the employee is their own supervisor.
     *
     * @param employee The employee object to update with group-related information.
     */
    private void resolveGroupInfo(Employee employee) {
        // Find the group where this employee is a member
        Group group = groupService.getGroupById(employee.getGroupId());
        int groupSupervisorEid = group.getGroupSupervisorEid();
        String groupName = group.getGroupName();
        
        // Set the supervisor field using groupSupervisorEid
        if (groupSupervisorEid == employee.getEmployeeId()) {
            // Employee is their own supervisor
            employee.setSupervisorName(employee.getName());
            employee.setDepartment(groupName);
            //employee.setSupervisorId(groupSupervisorEid); // Set the employee's own ID
        } else {
            // Fetch supervisor info by groupSupervisorEid
            Employee supervisor = employeeRepository.findByEmployeeId(groupSupervisorEid)
                .orElseThrow(() -> new RuntimeException("Supervisor not found with employeeId: " + groupSupervisorEid));
            employee.setSupervisorName(supervisor.getName());
            employee.setSupervisorId(groupSupervisorEid); // Set the supervisor's employee ID
            employee.setDepartment(groupName);
            employee.setIsSupervisor(groupService.existsByGroupSupervisorEid(employee.getEmployeeId()));            // Set isSupervisor field
        }
    }
    public int getSupervisorEidbyEmployeeId(int employeeId) {
        // Find the group where this employee is a member
        int supervisorEid = findGroupSupervisorEidByEmployeeId(employeeId);
        return supervisorEid;
    }
    public String getSupervisorOidbyEmployeeOid(String objectId) {
        int employeeId = getEmployeeIdById(objectId);
        // Find the group where this employee is a member
        int supervisorEid = findGroupSupervisorEidByEmployeeId(employeeId);
        String supervisorOid = getIdByEmployeeId(supervisorEid);
        
        return supervisorOid;
    }
                /**
     * Find a group by the member's employee ID.
     *
     * @param employeeId the employee ID to search for.
     * @return the group the employee belongs to, or null if not found.
     */
    public Group findByEmployeeId(int employeeId) {
        String groupId = findGroupIdbyEmployeeId(employeeId);
        return groupService.getGroupById(groupId);
    }
     /**
     * Retrieve the supervisor's employee ID for a group that a specific employee belongs to.
     *
     * This method checks which group the given employee is a member of and fetches the
     * employee ID of the supervisor of that group. If the employee does not belong to
     * any group, the method will return null.
     *
     * Edge cases:
     * - If the repository returns null, indicating the employee is not a member of any group,
     *   this method will also return null.
     * - Assumes that each employee is part of at most one group.
     *
     * @param employeeId the employee ID to search for.
     * @return the supervisor's employee ID if the employee belongs to a group, or null otherwise.
     */
    public Integer findGroupSupervisorEidByEmployeeId(int employeeId) {
        Group group = findByEmployeeId(employeeId);
        return group != null ? group.getGroupSupervisorEid() : null;
    }
        /**
     * Find group members supervised by a specific supervisor ID.
     *
     * @param supervisorId the ID of the supervisor.
     * @return a list of group members under the supervisor.
     */
    public List<Integer> findGroupMembersBySupervisorId(int supervisorId) {
        // Fetch group IDs supervised by the given supervisor ID
        List<String> groupIdList = groupService.findGroupIdListBySupervisorId(supervisorId);
        System.out.println("GroupIdList");
        System.out.println(groupIdList);
        
        // Initialize a list to store member IDs
        List<Integer> memberIdList = new ArrayList<>();
        for (String groupId : groupIdList){
            List<Integer> employeeIds = findEmployeeIdListbyGroupId(groupId);
            System.out.println("employeeIds");
            System.out.println(employeeIds);
            memberIdList.addAll(employeeIds); // Add all fetched employee IDs to the member list
        }
        
        return memberIdList;
    }
    /**
     * Resolve and set location-related information for an employee.
     *
     * This method fetches the location details for the given employee and populates
     * the employee's corresponding fields.
     *
     * Edge cases:
     * - Throws an exception if the location cannot be found.
     *
     * @param employee The employee object to update with location-related information.
     */
    private void resolveLocationInfo(Employee employee) {
        // Find the group where this employee is a member
        Location location = locationService.getLocationById(employee.getLocationId());
        String workhourOn = location.getWorkhourOn();
        String workhourOff = location.getWorkhourOff();
        String workhourHalf = location.getWorkhourHalf();
        String workplace = location.getWorkplace();

        employee.setWorkhourOn(workhourOn);
        employee.setWorkhourOff(workhourOff); 
        employee.setWorkhourHalf(workhourHalf); 
        employee.setWorkplace(workplace);
        
    }
    /**
     * Retrieve the workplace and work hour details for an employee by their ID.
     *
     * This method fetches the basic location details for an employee.
     *
     * Edge cases:
     * - Throws an exception if the employee is not found.
     *
     * @param employeeId The unique identifier of the employee.
     * @return A map containing the workplace and workhourOn details.
     */
    public Map<String, Object> getLocationAndWorkDetailsByEmployeeId(int employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with Id"));
        resolveLocationInfo(employee);
        // Extract the required details
        String workplace = employee.getWorkplace();
        String workhourOn = employee.getWorkhourOn();
        String workhourOff = employee.getWorkhourOff();
        String workhourHalf = employee.getWorkhourHalf();
        // Return both as a map or a custom object if preferred
        Map<String, Object> response = new HashMap<>();
        response.put("workplace", workplace);
        response.put("workhourOn", workhourOn);
        response.put("workhourOff", workhourOff);
        response.put("workhourHalf", workhourHalf);
        return response;
    }
     /**
     * Retrieve an employee by their phone number.
     *
     * Edge cases:
     * - Throws an exception if the employee is not found.
     *
     * @param phone The phone number of the employee.
     * @return The Employee object.
     */
    public Employee getEmployeeByPhone(String phone) {
        return employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Employee not found with phone: " + phone));
    }
    /**
     * Retrieve the workplace of an employee by their employee ID.
     *
     * Edge cases:
     * - Throws an exception if the employee is not found.
     *
     * @param employeeId The unique identifier of the employee.
     * @return The workplace of the employee.
     */
    public String getWorkplaceByEmployeeId(int employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .map(Employee::getWorkplace)
                .orElseThrow(() -> new RuntimeException("Employee not found with employeeId: " + employeeId));
    }
    /**
     * Retrieve minimal information for an employee by their employee ID.
     *
     * This method returns a map containing basic details such as employee ID,
     * name, and department.
     *
     * Edge cases:
     * - Throws an exception if the employee is not found.
     *
     * @param employeeId The unique identifier of the employee.
     * @return A map containing minimal employee details.
     */
    public Map<String, Object> getEmployeeMiniByEmployeeId(int employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
            .map(employee -> {
                Map<String, Object> miniEmployee = new HashMap<>();
                miniEmployee.put("employeeOid", employee.getId());
                miniEmployee.put("name", employee.getName());
                miniEmployee.put("department", employee.getDepartment());
                return miniEmployee;
            })
            .orElseThrow(() -> new RuntimeException("Employee not found with employeeId: " + employeeId));
    }
/**
     * Retrieve the employee ID using their unique ObjectId.
     *
     * Edge cases:
     * - Throws an exception if the employee is not found.
     *
     * @param objectId The string representation of the ObjectId.
     * @return The employee ID as an integer.
     */
    public int getEmployeeIdById(String objectId) {
        return employeeRepository.findEmployeeIdById(objectId)
                .map(Employee::getEmployeeId) // int type
                .orElseThrow(() -> new RuntimeException("Employee not found with Id" ));
    }

    public String getIdByEmployeeId(int employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .map(Employee::getId) 
                .orElseThrow(() -> new RuntimeException("Employee not found with Id" ));
    }

}
