package com.adcapsule.server52switch.backoffice.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.adacpsule.server52switch.shared.inputs.LocationInput;
import com.adcapsule.server52switch.backoffice.services.BackLocationService;


@Controller
public class BackLocationResolver {
    @Autowired
    private BackLocationService backLocationService;


    @MutationMapping
    public String updateOrNewLocation(@Argument String id, @Argument LocationInput input) {
        if (id==null){
            return backLocationService.genNewLocation(input);
        } else {
            // Call service to update the employee record
            return backLocationService.updateLocation(id,input);
        }
    }
    @MutationMapping
    public Boolean deleteLocationById(@Argument String id) {
        return backLocationService.deleteLocationById(id);
    }
}
