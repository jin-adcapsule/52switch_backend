package com.adcapsule.server52switch.backoffice.resolvers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.backoffice.dtos.GroupMembersDTO;
import com.adcapsule.server52switch.backoffice.services.BackOfficeService;
import com.adcapsule.server52switch.core.models.Attendance;
import com.adcapsule.server52switch.core.models.Dayoff;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.models.Group;
import com.adcapsule.server52switch.core.models.Location;
@Controller
public class BackOfficeResolver {

    private final BackOfficeService backOfficeService;

    @Autowired
    public BackOfficeResolver(BackOfficeService backOfficeService) {
        this.backOfficeService = backOfficeService;
    }

    @QueryMapping
    public List<Group> getMyAllGroups(@Argument String employeeOid) {
        return backOfficeService.getMyAllGroups(employeeOid);
    }
    @QueryMapping
    public List<GroupMembersDTO> getMyAllGroupsMembers(@Argument String employeeOid) {
        return backOfficeService.getMyAllGroupsMembers(employeeOid);
    }
    @QueryMapping
    public List<Location> getAllLocations() {
        return backOfficeService.getAllLocations();
    }
    
    @QueryMapping
    public List<Employee> getAllEmployees() {
        return backOfficeService.getAllEmployees();
    }
    @QueryMapping
    public List<Group> getAllGroups() {
        return backOfficeService.getAllGroups();
    }
    @QueryMapping
    public List<Dayoff> getAllDayoffs() {
        return backOfficeService.getAllDayoffs();
    }
    @QueryMapping
    public List<Attendance> getAllAttendances() {
        return backOfficeService.getAllAttendances();
    }
}
