package com.adcapsule.server52switch.backoffice.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adacpsule.server52switch.shared.inputs.EmployeeInput;
import com.adcapsule.server52switch.backoffice.services.BackEmployeeService;

@Controller
public class BackEmployeeResolver {
    @Autowired
    private BackEmployeeService backEmployeeService;
    @MutationMapping
    public String updateEmployee(@Argument String id, @Argument EmployeeInput input) {
        // Call service to update the employee record
        return backEmployeeService
        .updateEmployee(id,input);
    }
}
