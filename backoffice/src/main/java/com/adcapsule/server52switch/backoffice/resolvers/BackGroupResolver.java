package com.adcapsule.server52switch.backoffice.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adacpsule.server52switch.shared.inputs.GroupInput;
import com.adcapsule.server52switch.backoffice.services.BackGroupService;


@Controller
public class BackGroupResolver {
    @Autowired
    private BackGroupService backGroupService;


    @MutationMapping
    public String updateGroup(@Argument String id, @Argument GroupInput input) {
        // Call service to update the employee record
        return backGroupService.updateGroup(id,input);
    }
}
