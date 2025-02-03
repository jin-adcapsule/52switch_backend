package com.adcapsule.server52switch.core.resolvers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.adcapsule.server52switch.core.dtos.EmployeeDTO;
import com.adcapsule.server52switch.core.dtos.LocationInfoDTO;
import com.adcapsule.server52switch.core.models.Employee;
import com.adcapsule.server52switch.core.services.EmployeeService;

@Controller
public class EmployeeResolver {


    @Autowired
    private EmployeeService employeeService;
    @QueryMapping
    public EmployeeDTO getEmployeeInfo(@Argument String employeeOid) {
        return employeeService.getEmployeeDTOById(employeeOid);
    }

    // @QueryMapping
    // public Map<String, Object> getEmployeeInfo_mini(@Argument int employeeId) {
    //     return employeeService.getEmployeeMiniByEmployeeId(employeeId);
    // }
    @QueryMapping
    public Employee getObjectIdByPhone(@Argument String phone) {
        return employeeService.getEmployeeByPhone(phone);
    }

    @QueryMapping
    public String getWorkplaceByEmployeeId(@Argument int employeeId) {
        return employeeService.getWorkplaceByEmployeeId(employeeId);
    }
    @QueryMapping
    public LocationInfoDTO getLocationAndWorkDetailsByEmployeeOid(@Argument String employeeOid) {
        return employeeService.getLocationAndWorkDetailsByEmployeeOid(employeeOid);
    }
    @QueryMapping
    public LocationInfoDTO getLocationInfo(@Argument String employeeOid) {
        return employeeService.getLocationAndWorkDetailsByEmployeeOid(employeeOid);
    }
    @QueryMapping
    public int getEmployeeIdById(String _id) {
        return employeeService.getEmployeeIdById(_id);
    }


}
