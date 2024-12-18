package com.example._switch_backend.resolvers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example._switch_backend.models.Employee;
import com.example._switch_backend.services.EmployeeService;

@Controller
public class EmployeeResolver {


    @Autowired
    private EmployeeService employeeService;
    @QueryMapping
    public Employee getEmployeeInfo(@Argument String objectId) {
        return employeeService.getEmployeeById(objectId);
    }

    @QueryMapping
    public Map<String, Object> getEmployeeInfo_mini(@Argument int employeeId) {
        return employeeService.getEmployeeMiniByEmployeeId(employeeId);
    }
    @QueryMapping
    public Employee getObjectIdByPhone(@Argument String phone) {
        return employeeService.getEmployeeByPhone(phone);
    }

    @QueryMapping
    public String getWorkplaceByEmployeeId(@Argument int employeeId) {
        return employeeService.getWorkplaceByEmployeeId(employeeId);
    }
    @QueryMapping
    public Map<String, Object> getLocationAndWorkDetailsByEmployeeId(@Argument int employeeId) {
        return employeeService.getLocationAndWorkDetailsByEmployeeId(employeeId);
    }
    @QueryMapping
    public int getEmployeeIdById(String _id) {
        return employeeService.getEmployeeIdById(_id);
    }

}
