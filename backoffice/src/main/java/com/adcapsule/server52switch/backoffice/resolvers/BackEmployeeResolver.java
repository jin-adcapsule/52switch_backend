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
    public String updateOrNewEmployee(@Argument String id, @Argument EmployeeInput input) {
        if (id==null){
            return backEmployeeService.genNewEmployee(input);
        } else {
            // Call service to update the employee record
            return backEmployeeService
            .updateEmployee(id,input);
        }
    }
    @MutationMapping
    public Boolean deleteEmployeeById(@Argument String id) {
        return backEmployeeService.deleteEmployeeById(id);
        
    }
}
